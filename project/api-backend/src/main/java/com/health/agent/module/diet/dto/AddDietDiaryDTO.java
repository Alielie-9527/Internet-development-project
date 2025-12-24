package com.health.agent.module.diet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 添加饮食日记DTO
 */
@Data
public class AddDietDiaryDTO {
    
    @NotNull(message = "食物ID不能为空")
    private Long foodId;
    
    @NotBlank(message = "用餐类型不能为空")
    private String mealType; // breakfast, lunch, dinner, snack
    
    @NotNull(message = "用餐时间不能为空")
    private LocalDateTime mealTime;
    
    @NotNull(message = "食用量不能为空")
    @DecimalMin(value = "0.01", message = "食用量必须大于0")
    private BigDecimal amount;
    
    @NotBlank(message = "单位不能为空")
    private String unit; // g, ml, 份, 个
    
    private String notes; // 备注
    
    private String imageUrl; // 照片URL
}
