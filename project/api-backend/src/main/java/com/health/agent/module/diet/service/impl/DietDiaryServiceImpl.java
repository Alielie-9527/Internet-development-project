package com.health.agent.module.diet.service.impl;

import com.health.agent.common.exception.BusinessException;
import com.health.agent.module.diet.dto.AddDietDiaryDTO;
import com.health.agent.module.diet.dto.DietDiaryQueryDTO;
import com.health.agent.module.diet.dto.UpdateDietDiaryDTO;
import com.health.agent.module.diet.entity.DietDiary;
import com.health.agent.module.diet.mapper.DietDiaryMapper;
import com.health.agent.module.diet.service.IDietDiaryService;
import com.health.agent.module.diet.vo.DietDiaryVO;
import com.health.agent.module.diet.vo.DietStatisticsVO;
import com.health.agent.module.food.entity.Food;
import com.health.agent.module.food.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 饮食日记服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DietDiaryServiceImpl implements IDietDiaryService {
    
    private final DietDiaryMapper dietDiaryMapper;
    private final FoodMapper foodMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDietDiary(Long userId, AddDietDiaryDTO dto) {
        // 验证食物存在
        Food food = foodMapper.selectById(dto.getFoodId());
        if (food == null) {
            throw new BusinessException("食物不存在");
        }
        
        // 计算营养成分（按比例）
        BigDecimal ratio = dto.getAmount().divide(food.getServingSize(), 4, RoundingMode.HALF_UP);
        
        DietDiary diary = new DietDiary();
        diary.setUserId(userId);
        diary.setFoodId(dto.getFoodId());
        diary.setMealType(dto.getMealType());
        diary.setMealTime(dto.getMealTime());
        diary.setAmount(dto.getAmount());
        diary.setUnit(dto.getUnit());
        diary.setNotes(dto.getNotes());
        diary.setImageUrl(dto.getImageUrl());
        
        // 计算营养成分
        diary.setCalories(food.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        diary.setProtein(food.getProtein().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        diary.setCarbohydrate(food.getCarbohydrate().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        diary.setFat(food.getFat().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        // 设置冗余的食物名称，避免数据库中 `food_name` 非空导致插入失败
        diary.setFoodName(food.getName());

        dietDiaryMapper.insert(diary);
        log.info("用户 {} 添加饮食日记成功，ID: {}", userId, diary.getId());
        
        return diary.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDietDiary(Long userId, UpdateDietDiaryDTO dto) {
        // 验证日记存在且属于当前用户
        DietDiary existingDiary = dietDiaryMapper.selectById(dto.getId());
        if (existingDiary == null) {
            throw new BusinessException("饮食日记不存在");
        }
        if (!existingDiary.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此日记");
        }
        
        DietDiary diary = new DietDiary();
        diary.setId(dto.getId());
        
        // 如果修改了食物或数量，重新计算营养成分
        if (dto.getFoodId() != null || dto.getAmount() != null) {
            Long foodId = dto.getFoodId() != null ? dto.getFoodId() : existingDiary.getFoodId();
            BigDecimal amount = dto.getAmount() != null ? dto.getAmount() : existingDiary.getAmount();
            
            Food food = foodMapper.selectById(foodId);
            if (food == null) {
                throw new BusinessException("食物不存在");
            }
            
            BigDecimal ratio = amount.divide(food.getServingSize(), 4, RoundingMode.HALF_UP);
            diary.setFoodId(foodId);
            diary.setFoodName(food.getName());
            diary.setAmount(amount);
            diary.setCalories(food.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
            diary.setProtein(food.getProtein().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
            diary.setCarbohydrate(food.getCarbohydrate().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
            diary.setFat(food.getFat().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }
        
        if (dto.getMealType() != null) {
            diary.setMealType(dto.getMealType());
        }
        if (dto.getMealTime() != null) {
            diary.setMealTime(dto.getMealTime());
        }
        if (dto.getUnit() != null) {
            diary.setUnit(dto.getUnit());
        }
        if (dto.getNotes() != null) {
            diary.setNotes(dto.getNotes());
        }
        if (dto.getImageUrl() != null) {
            diary.setImageUrl(dto.getImageUrl());
        }
        
        diary.setUpdatedAt(LocalDateTime.now());
        
        dietDiaryMapper.updateById(diary);
        log.info("用户 {} 更新饮食日记成功，ID: {}", userId, dto.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDietDiary(Long userId, Long id) {
        // 验证日记存在且属于当前用户
        DietDiary diary = dietDiaryMapper.selectById(id);
        if (diary == null) {
            throw new BusinessException("饮食日记不存在");
        }
        if (!diary.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此日记");
        }
        
        dietDiaryMapper.deleteById(id);
        log.info("用户 {} 删除饮食日记成功，ID: {}", userId, id);
    }
    
    @Override
    public DietDiaryVO getDietDiaryById(Long userId, Long id) {
        DietDiaryVO vo = dietDiaryMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("饮食日记不存在");
        }
        if (!vo.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此日记");
        }
        
        // 设置用餐类型描述
        vo.setMealTypeDesc(getMealTypeDesc(vo.getMealType()));
        
        return vo;
    }
    
    @Override
    public List<DietDiaryVO> getDietDiaryList(Long userId, DietDiaryQueryDTO query) {
        List<DietDiaryVO> list = dietDiaryMapper.selectList(userId, query);
        
        // 设置用餐类型描述
        list.forEach(vo -> vo.setMealTypeDesc(getMealTypeDesc(vo.getMealType())));
        
        return list;
    }
    
    @Override
    public List<DietDiaryVO> getDietDiaryByDate(Long userId, LocalDate date) {
        List<DietDiaryVO> list = dietDiaryMapper.selectByUserAndDate(userId, date);
        
        // 设置用餐类型描述
        list.forEach(vo -> vo.setMealTypeDesc(getMealTypeDesc(vo.getMealType())));
        
        return list;
    }
    
    @Override
    public List<DietDiaryVO> getDietDiaryByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<DietDiaryVO> list = dietDiaryMapper.selectByUserAndDateRange(userId, startDate, endDate);
        
        // 设置用餐类型描述
        list.forEach(vo -> vo.setMealTypeDesc(getMealTypeDesc(vo.getMealType())));
        
        return list;
    }
    
    @Override
    public DietStatisticsVO getDietStatistics(Long userId, LocalDate date) {
        List<DietDiaryVO> diaries = dietDiaryMapper.selectByUserAndDate(userId, date);
        
        DietStatisticsVO statistics = new DietStatisticsVO();
        statistics.setDate(date);
        
        if (diaries.isEmpty()) {
            statistics.setTotalCalories(BigDecimal.ZERO);
            statistics.setTotalProtein(BigDecimal.ZERO);
            statistics.setTotalCarbohydrate(BigDecimal.ZERO);
            statistics.setTotalFat(BigDecimal.ZERO);
            statistics.setMealCount(0);
            statistics.setMeals(new ArrayList<>());
            return statistics;
        }
        
        // 计算总营养
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        
        for (DietDiaryVO diary : diaries) {
            totalCalories = totalCalories.add(diary.getCalories());
            totalProtein = totalProtein.add(diary.getProtein());
            totalCarbs = totalCarbs.add(diary.getCarbohydrate());
            totalFat = totalFat.add(diary.getFat());
        }
        
        statistics.setTotalCalories(totalCalories.setScale(2, RoundingMode.HALF_UP));
        statistics.setTotalProtein(totalProtein.setScale(2, RoundingMode.HALF_UP));
        statistics.setTotalCarbohydrate(totalCarbs.setScale(2, RoundingMode.HALF_UP));
        statistics.setTotalFat(totalFat.setScale(2, RoundingMode.HALF_UP));
        
        // 按餐次分组统计
        Map<String, List<DietDiaryVO>> mealGroups = diaries.stream()
            .collect(Collectors.groupingBy(DietDiaryVO::getMealType));
        
        statistics.setMealCount(mealGroups.size());
        
        List<DietStatisticsVO.MealSummary> meals = new ArrayList<>();
        for (Map.Entry<String, List<DietDiaryVO>> entry : mealGroups.entrySet()) {
            String mealType = entry.getKey();
            List<DietDiaryVO> mealDiaries = entry.getValue();
            
            DietStatisticsVO.MealSummary mealSummary = new DietStatisticsVO.MealSummary();
            mealSummary.setMealType(mealType);
            mealSummary.setMealTypeDesc(getMealTypeDesc(mealType));
            
            BigDecimal mealCalories = BigDecimal.ZERO;
            BigDecimal mealProtein = BigDecimal.ZERO;
            BigDecimal mealCarbs = BigDecimal.ZERO;
            BigDecimal mealFat = BigDecimal.ZERO;
            
            for (DietDiaryVO diary : mealDiaries) {
                mealCalories = mealCalories.add(diary.getCalories());
                mealProtein = mealProtein.add(diary.getProtein());
                mealCarbs = mealCarbs.add(diary.getCarbohydrate());
                mealFat = mealFat.add(diary.getFat());
            }
            
            mealSummary.setCalories(mealCalories.setScale(2, RoundingMode.HALF_UP));
            mealSummary.setProtein(mealProtein.setScale(2, RoundingMode.HALF_UP));
            mealSummary.setCarbohydrate(mealCarbs.setScale(2, RoundingMode.HALF_UP));
            mealSummary.setFat(mealFat.setScale(2, RoundingMode.HALF_UP));
            mealSummary.setItemCount(mealDiaries.size());
            
            meals.add(mealSummary);
        }
        
        statistics.setMeals(meals);
        
        return statistics;
    }
    
    /**
     * 获取用餐类型描述
     */
    private String getMealTypeDesc(String mealType) {
        return switch (mealType) {
            case "breakfast" -> "早餐";
            case "lunch" -> "午餐";
            case "dinner" -> "晚餐";
            case "snack" -> "加餐";
            default -> mealType;
        };
    }
}
