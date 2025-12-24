package com.health.agent.module.weight.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 添加体重记录DTO
 */
@Data
public class AddWeightRecordDTO {
    
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;
    
    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "0.1", message = "体重必须大于0")
    private BigDecimal weight; // 体重(kg)
    
    private BigDecimal bodyFatRate; // 体脂率(%)
    
    private BigDecimal muscleMass; // 肌肉量(kg)
    
    private BigDecimal boneMass; // 骨量(kg)
    
    private BigDecimal waterRate; // 水分率(%)
    
    private BigDecimal visceralFat; // 内脏脂肪等级
    
    private BigDecimal basalMetabolism; // 基础代谢(kcal)
    
    private String notes; // 备注
}
