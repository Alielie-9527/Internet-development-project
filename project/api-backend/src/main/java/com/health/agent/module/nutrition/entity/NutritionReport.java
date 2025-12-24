package com.health.agent.module.nutrition.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 营养分析报告实体类
 */
@Data
public class NutritionReport {
    
    private Long id;
    
    private Long userId;
    
    private LocalDate reportDate;
    
    private String reportType; // daily-每日 weekly-每周 monthly-每月
    
    private BigDecimal totalCalories; // 总热量
    
    private BigDecimal totalProtein; // 总蛋白质(g)
    
    private BigDecimal totalCarbohydrate; // 总碳水化合物(g)
    
    private BigDecimal totalFat; // 总脂肪(g)
    
    private BigDecimal caloriesBurned; // 消耗热量
    
    private BigDecimal netCalories; // 净热量
    
    private BigDecimal goalCompletionRate; // 目标完成率(%)
    
    private Integer nutritionBalanceScore; // 营养均衡评分(0-100)
    
    private String aiAdvice; // AI建议
    
    private LocalDateTime createdAt;
}
