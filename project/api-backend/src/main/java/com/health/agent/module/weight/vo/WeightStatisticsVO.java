package com.health.agent.module.weight.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 体重统计VO
 */
@Data
@Builder
public class WeightStatisticsVO {
    
    private BigDecimal currentWeight; // 当前体重
    
    private BigDecimal currentBmi; // 当前BMI
    
    private String bmiStatus; // BMI状态
    
    private BigDecimal targetWeight; // 目标体重
    
    private BigDecimal weightToTarget; // 距离目标(kg)
    
    private BigDecimal last7DaysChange; // 近7天变化
    
    private BigDecimal last30DaysChange; // 近30天变化
    
    private BigDecimal totalChange; // 总变化
    
    private Integer totalRecords; // 总记录数
    
    private String healthGoal; // 健康目标
}
