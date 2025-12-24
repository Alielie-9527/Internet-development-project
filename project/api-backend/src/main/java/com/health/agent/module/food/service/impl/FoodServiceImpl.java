package com.health.agent.module.food.service.impl;

import com.health.agent.common.exception.BusinessException;
import com.health.agent.module.food.dto.FoodCreateDTO;
import com.health.agent.module.food.dto.FoodSearchDTO;
import com.health.agent.module.food.entity.Food;
import com.health.agent.module.food.mapper.FoodMapper;
import com.health.agent.module.food.service.IFoodService;
import com.health.agent.module.food.vo.FoodVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 食物服务实现类
 */
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements IFoodService {

    private final FoodMapper foodMapper;
    
    // 分类中文名映射
    private static final Map<String, String> CATEGORY_NAME_MAP = Map.of(
        "staple", "主食",
        "vegetable", "蔬菜",
        "fruit", "水果",
        "protein", "蛋白质",
        "dairy", "乳制品",
        "snack", "零食",
        "drink", "饮品"
    );

    @Override
    public FoodVO getFoodById(Long id) {
        Food food = foodMapper.findById(id);
        if (food == null) {
            throw new BusinessException("食物不存在");
        }
        return convertToVO(food);
    }

    @Override
    public FoodVO getFoodByBarcode(String barcode) {
        Food food = foodMapper.findByBarcode(barcode);
        if (food == null) {
            throw new BusinessException("未找到该条形码对应的食物");
        }
        return convertToVO(food);
    }

    @Override
    public Map<String, Object> searchFood(FoodSearchDTO dto) {
        // 计算分页偏移量
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            int offset = (dto.getPageNum() - 1) * dto.getPageSize();
            dto.setPageNum(offset); // 复用pageNum字段存储offset
        }
        
        // 查询数据
        List<Food> foodList = foodMapper.search(dto);
        int total = foodMapper.countSearch(dto);
        
        // 转换为VO
        List<FoodVO> voList = foodList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum() != null ? (dto.getPageNum() / dto.getPageSize() + 1) : 1);
        result.put("pageSize", dto.getPageSize() != null ? dto.getPageSize() : voList.size());
        
        return result;
    }

    @Override
    public List<FoodVO> getFoodByCategory(String category) {
        List<Food> foodList = foodMapper.findByCategory(category);
        return foodList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getCategoryStats() {
        // 获取所有食物
        List<Food> allFoods = foodMapper.findAll(null, null);
        
        // 按分类统计
        Map<String, Long> stats = allFoods.stream()
                .collect(Collectors.groupingBy(Food::getCategory, Collectors.counting()));
        
        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FoodVO createFood(FoodCreateDTO dto) {
        // 检查食物名称是否已存在
        Food existFood = foodMapper.findByName(dto.getName());
        if (existFood != null) {
            throw new BusinessException("该食物已存在");
        }
        
        // 如果有条形码，检查条形码是否已存在
        if (dto.getBarcode() != null && !dto.getBarcode().isEmpty()) {
            Food existBarcode = foodMapper.findByBarcode(dto.getBarcode());
            if (existBarcode != null) {
                throw new BusinessException("该条形码已被使用");
            }
        }
        
        // 创建食物
        Food food = new Food();
        BeanUtils.copyProperties(dto, food);
        food.setIsVerified(0); // 用户创建的食物默认未认证
        
        foodMapper.insert(food);
        
        return convertToVO(food);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FoodVO updateFood(Long id, FoodCreateDTO dto) {
        Food food = foodMapper.findById(id);
        if (food == null) {
            throw new BusinessException("食物不存在");
        }
        
        // 检查名称是否与其他食物重复
        Food existFood = foodMapper.findByName(dto.getName());
        if (existFood != null && !existFood.getId().equals(id)) {
            throw new BusinessException("该食物名称已存在");
        }
        
        // 更新食物信息
        BeanUtils.copyProperties(dto, food);
        foodMapper.update(food);
        
        return convertToVO(food);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFood(Long id) {
        Food food = foodMapper.findById(id);
        if (food == null) {
            throw new BusinessException("食物不存在");
        }
        
        foodMapper.deleteById(id);
    }
    
    /**
     * 将Food实体转换为FoodVO
     */
    private FoodVO convertToVO(Food food) {
        FoodVO vo = FoodVO.builder()
                .id(food.getId())
                .name(food.getName())
                .category(food.getCategory())
                .categoryName(CATEGORY_NAME_MAP.getOrDefault(food.getCategory(), food.getCategory()))
                .brand(food.getBrand())
                .barcode(food.getBarcode())
                .unit(food.getUnit())
                .servingSize(food.getServingSize())
                .calories(food.getCalories())
                .protein(food.getProtein())
                .carbohydrate(food.getCarbohydrate())
                .fat(food.getFat())
                .fiber(food.getFiber())
                .sodium(food.getSodium())
                .sugar(food.getSugar())
                .vitaminA(food.getVitaminA())
                .vitaminC(food.getVitaminC())
                .calcium(food.getCalcium())
                .iron(food.getIron())
                .imageUrl(food.getImageUrl())
                .description(food.getDescription())
                .isVerified(food.getIsVerified() == 1)
                .source(food.getSource())
                .createdAt(food.getCreatedAt())
                .build();
        
        return vo;
    }
}
