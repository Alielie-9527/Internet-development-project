package com.health.agent.module.weight.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体重记录实体类
 */
@Data
public class WeightRecord {
    
    private Long id;
    
    private Long userId;
    
    private LocalDate recordDate; // 记录日期
    
    private BigDecimal weight; // 体重(kg)
    
    private BigDecimal bmi; // BMI指数
    
    private BigDecimal bodyFatRate; // 体脂率(%)
    
    private BigDecimal muscleMass; // 肌肉量(kg)
    
    private BigDecimal boneMass; // 骨量(kg)
    
    private BigDecimal waterRate; // 水分率(%)
    
    private BigDecimal visceralFat; // 内脏脂肪等级
    
    private BigDecimal basalMetabolism; // 基础代谢(kcal)
    
    private String notes; // 备注
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
