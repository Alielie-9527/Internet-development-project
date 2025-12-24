package com.health.agent.module.weight.service.impl;

import com.health.agent.common.exception.BusinessException;
import com.health.agent.module.user.entity.User;
import com.health.agent.module.user.mapper.UserMapper;
import com.health.agent.module.weight.dto.AddWeightRecordDTO;
import com.health.agent.module.weight.dto.UpdateWeightRecordDTO;
import com.health.agent.module.weight.dto.WeightRecordQueryDTO;
import com.health.agent.module.weight.entity.WeightRecord;
import com.health.agent.module.weight.mapper.WeightRecordMapper;
import com.health.agent.module.weight.service.IWeightRecordService;
import com.health.agent.module.weight.vo.WeightRecordVO;
import com.health.agent.module.weight.vo.WeightStatisticsVO;
import com.health.agent.module.weight.vo.WeightTrendVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体重记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeightRecordServiceImpl implements IWeightRecordService {
    
    private final WeightRecordMapper weightRecordMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addWeightRecord(Long userId, AddWeightRecordDTO dto) {
        // 检查当天是否已有记录
        WeightRecord existing = weightRecordMapper.selectByUserAndDate(userId, dto.getRecordDate());
        if (existing != null) {
            throw new BusinessException("该日期已存在体重记录，请修改或删除后重试");
        }
        
        // 获取用户身高用于计算BMI
        User user = userMapper.selectById(userId);
        
        WeightRecord record = new WeightRecord();
        record.setUserId(userId);
        record.setRecordDate(dto.getRecordDate());
        record.setWeight(dto.getWeight());
        record.setBodyFatRate(dto.getBodyFatRate());
        record.setMuscleMass(dto.getMuscleMass());
        record.setBoneMass(dto.getBoneMass());
        record.setWaterRate(dto.getWaterRate());
        record.setVisceralFat(dto.getVisceralFat());
        record.setBasalMetabolism(dto.getBasalMetabolism());
        record.setNotes(dto.getNotes());
        
        // 计算BMI (BMI = 体重kg / (身高m)^2)
        if (user != null && user.getHeight() != null && user.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = user.getHeight().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal bmi = dto.getWeight().divide(heightM.multiply(heightM), 2, RoundingMode.HALF_UP);
            record.setBmi(bmi);
        }
        
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        weightRecordMapper.insert(record);
        log.info("用户 {} 添加体重记录成功，ID: {}", userId, record.getId());
        
        // 同步更新用户表的体重字段
        if (user != null) {
            user.setWeight(dto.getWeight());
            userMapper.updateById(user);
        }
        
        return record.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeightRecord(Long userId, UpdateWeightRecordDTO dto) {
        // 验证记录存在且属于当前用户
        WeightRecord existingRecord = weightRecordMapper.selectById(dto.getId());
        if (existingRecord == null) {
            throw new BusinessException("体重记录不存在");
        }
        if (!existingRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此记录");
        }
        
        WeightRecord record = new WeightRecord();
        record.setId(dto.getId());
        
        if (dto.getRecordDate() != null) {
            record.setRecordDate(dto.getRecordDate());
        }
        
        if (dto.getWeight() != null) {
            record.setWeight(dto.getWeight());
            
            // 重新计算BMI
            User user = userMapper.selectById(userId);
            if (user != null && user.getHeight() != null && user.getHeight().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal heightM = user.getHeight().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal bmi = dto.getWeight().divide(heightM.multiply(heightM), 2, RoundingMode.HALF_UP);
                record.setBmi(bmi);
            }
        }
        
        if (dto.getBodyFatRate() != null) {
            record.setBodyFatRate(dto.getBodyFatRate());
        }
        if (dto.getMuscleMass() != null) {
            record.setMuscleMass(dto.getMuscleMass());
        }
        if (dto.getBoneMass() != null) {
            record.setBoneMass(dto.getBoneMass());
        }
        if (dto.getWaterRate() != null) {
            record.setWaterRate(dto.getWaterRate());
        }
        if (dto.getVisceralFat() != null) {
            record.setVisceralFat(dto.getVisceralFat());
        }
        if (dto.getBasalMetabolism() != null) {
            record.setBasalMetabolism(dto.getBasalMetabolism());
        }
        if (dto.getNotes() != null) {
            record.setNotes(dto.getNotes());
        }
        
        record.setUpdatedAt(LocalDateTime.now());
        
        weightRecordMapper.updateById(record);
        log.info("用户 {} 更新体重记录成功，ID: {}", userId, dto.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWeightRecord(Long userId, Long id) {
        // 验证记录存在且属于当前用户
        WeightRecord record = weightRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("体重记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此记录");
        }
        
        weightRecordMapper.deleteById(id);
        log.info("用户 {} 删除体重记录成功，ID: {}", userId, id);
    }
    
    @Override
    public WeightRecordVO getWeightRecordById(Long userId, Long id) {
        WeightRecord record = weightRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("体重记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此记录");
        }
        
        return toVO(record, userId);
    }
    
    @Override
    public List<WeightRecordVO> getWeightRecordList(Long userId, WeightRecordQueryDTO query) {
        List<WeightRecord> list = weightRecordMapper.selectList(userId, query);
        
        return list.stream()
                .map(record -> toVO(record, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public WeightRecordVO getLatestWeightRecord(Long userId) {
        WeightRecord record = weightRecordMapper.selectLatestByUser(userId);
        if (record == null) {
            return null;
        }
        return toVO(record, userId);
    }
    
    @Override
    public WeightTrendVO getWeightTrend(Long userId, LocalDate startDate, LocalDate endDate) {
        List<WeightRecord> records = weightRecordMapper.selectByDateRange(userId, startDate, endDate);
        
        if (records == null || records.isEmpty()) {
            return WeightTrendVO.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .recordCount(0)
                    .dataPoints(new ArrayList<>())
                    .build();
        }
        
        // 按日期排序
        records.sort(Comparator.comparing(WeightRecord::getRecordDate));
        
        BigDecimal startWeight = records.get(0).getWeight();
        BigDecimal currentWeight = records.get(records.size() - 1).getWeight();
        
        BigDecimal minWeight = records.stream()
                .map(WeightRecord::getWeight)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal maxWeight = records.stream()
                .map(WeightRecord::getWeight)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal sumWeight = records.stream()
                .map(WeightRecord::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgWeight = sumWeight.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
        
        BigDecimal totalChange = currentWeight.subtract(startWeight);
        BigDecimal changeRate = startWeight.compareTo(BigDecimal.ZERO) > 0 
                ? totalChange.divide(startWeight, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 构建数据点
        List<WeightTrendVO.DataPoint> dataPoints = records.stream()
                .map(r -> WeightTrendVO.DataPoint.builder()
                        .date(r.getRecordDate())
                        .weight(r.getWeight())
                        .bmi(r.getBmi())
                        .bodyFatRate(r.getBodyFatRate())
                        .build())
                .collect(Collectors.toList());
        
        return WeightTrendVO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .currentWeight(currentWeight)
                .startWeight(startWeight)
                .minWeight(minWeight)
                .maxWeight(maxWeight)
                .avgWeight(avgWeight)
                .totalChange(totalChange)
                .changeRate(changeRate.setScale(2, RoundingMode.HALF_UP))
                .recordCount(records.size())
                .dataPoints(dataPoints)
                .build();
    }
    
    @Override
    public WeightStatisticsVO getWeightStatistics(Long userId) {
        User user = userMapper.selectById(userId);
        WeightRecord latest = weightRecordMapper.selectLatestByUser(userId);
        
        if (latest == null) {
            return null;
        }
        
        BigDecimal currentWeight = latest.getWeight();
        BigDecimal currentBmi = latest.getBmi();
        String bmiStatus = getBmiStatus(currentBmi);
        
        // 计算近7天变化
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        WeightRecord record7Days = weightRecordMapper.selectPreviousRecord(userId, sevenDaysAgo);
        BigDecimal last7DaysChange = record7Days != null 
                ? currentWeight.subtract(record7Days.getWeight()) 
                : BigDecimal.ZERO;
        
        // 计算近30天变化
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        WeightRecord record30Days = weightRecordMapper.selectPreviousRecord(userId, thirtyDaysAgo);
        BigDecimal last30DaysChange = record30Days != null 
                ? currentWeight.subtract(record30Days.getWeight()) 
                : BigDecimal.ZERO;
        
        // 获取第一条记录计算总变化
        List<WeightRecord> allRecords = weightRecordMapper.selectList(userId, new WeightRecordQueryDTO());
        BigDecimal totalChange = BigDecimal.ZERO;
        if (!allRecords.isEmpty()) {
            WeightRecord first = allRecords.get(allRecords.size() - 1);
            totalChange = currentWeight.subtract(first.getWeight());
        }
        
        int totalRecords = weightRecordMapper.countByUser(userId);
        
        return WeightStatisticsVO.builder()
                .currentWeight(currentWeight)
                .currentBmi(currentBmi)
                .bmiStatus(bmiStatus)
                .targetWeight(user != null ? calculateTargetWeight(user) : null)
                .weightToTarget(user != null ? calculateWeightToTarget(currentWeight, user) : null)
                .last7DaysChange(last7DaysChange.setScale(2, RoundingMode.HALF_UP))
                .last30DaysChange(last30DaysChange.setScale(2, RoundingMode.HALF_UP))
                .totalChange(totalChange.setScale(2, RoundingMode.HALF_UP))
                .totalRecords(totalRecords)
                .healthGoal(user != null ? user.getHealthGoal() : null)
                .build();
    }
    
    /**
     * 转换为VO
     */
    private WeightRecordVO toVO(WeightRecord record, Long userId) {
        WeightRecordVO vo = new WeightRecordVO();
        vo.setId(record.getId());
        vo.setUserId(record.getUserId());
        vo.setRecordDate(record.getRecordDate());
        vo.setWeight(record.getWeight());
        vo.setBmi(record.getBmi());
        vo.setBmiStatus(getBmiStatus(record.getBmi()));
        vo.setBodyFatRate(record.getBodyFatRate());
        vo.setMuscleMass(record.getMuscleMass());
        vo.setBoneMass(record.getBoneMass());
        vo.setWaterRate(record.getWaterRate());
        vo.setVisceralFat(record.getVisceralFat());
        vo.setBasalMetabolism(record.getBasalMetabolism());
        vo.setNotes(record.getNotes());
        vo.setCreatedAt(record.getCreatedAt());
        vo.setUpdatedAt(record.getUpdatedAt());
        
        // 计算体重变化
        WeightRecord previous = weightRecordMapper.selectPreviousRecord(userId, record.getRecordDate());
        if (previous != null) {
            BigDecimal change = record.getWeight().subtract(previous.getWeight());
            vo.setWeightChange(change.setScale(2, RoundingMode.HALF_UP));
        } else {
            vo.setWeightChange(BigDecimal.ZERO);
        }
        
        return vo;
    }
    
    /**
     * 获取BMI状态
     */
    private String getBmiStatus(BigDecimal bmi) {
        if (bmi == null) {
            return "unknown";
        }
        if (bmi.compareTo(BigDecimal.valueOf(18.5)) < 0) {
            return "underweight"; // 偏瘦
        } else if (bmi.compareTo(BigDecimal.valueOf(24)) < 0) {
            return "normal"; // 正常
        } else if (bmi.compareTo(BigDecimal.valueOf(28)) < 0) {
            return "overweight"; // 超重
        } else {
            return "obese"; // 肥胖
        }
    }
    
    /**
     * 计算目标体重（根据健康目标）
     */
    private BigDecimal calculateTargetWeight(User user) {
        if (user.getHeight() == null) {
            return null;
        }
        
        // 简单计算：使用标准BMI 22
        BigDecimal heightM = user.getHeight().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(22).multiply(heightM).multiply(heightM).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算距离目标体重
     */
    private BigDecimal calculateWeightToTarget(BigDecimal currentWeight, User user) {
        BigDecimal targetWeight = calculateTargetWeight(user);
        if (targetWeight == null) {
            return null;
        }
        return currentWeight.subtract(targetWeight).setScale(2, RoundingMode.HALF_UP);
    }
}
