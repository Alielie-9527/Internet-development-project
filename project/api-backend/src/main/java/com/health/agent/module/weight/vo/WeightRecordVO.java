package com.health.agent.module.weight.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体重记录VO
 */
@Data
public class WeightRecordVO {
    
    private Long id;
    
    private Long userId;
    
    private LocalDate recordDate; // 记录日期
    
    private BigDecimal weight; // 体重(kg)
    
    private BigDecimal bmi; // BMI指数
    
    private String bmiStatus; // BMI状态: underweight-偏瘦 normal-正常 overweight-超重 obese-肥胖
    
    private BigDecimal bodyFatRate; // 体脂率(%)
    
    private BigDecimal muscleMass; // 肌肉量(kg)
    
    private BigDecimal boneMass; // 骨量(kg)
    
    private BigDecimal waterRate; // 水分率(%)
    
    private BigDecimal visceralFat; // 内脏脂肪等级
    
    private BigDecimal basalMetabolism; // 基础代谢(kcal)
    
    private String notes; // 备注
    
    private BigDecimal weightChange; // 体重变化(kg) 相比上一次
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
