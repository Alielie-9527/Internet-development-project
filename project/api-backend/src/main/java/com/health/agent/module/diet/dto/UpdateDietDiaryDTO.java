package com.health.agent.module.diet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新饮食日记DTO
 */
@Data
public class UpdateDietDiaryDTO {
    
    @NotNull(message = "日记ID不能为空")
    private Long id;
    
    private Long foodId;
    
    private String mealType; // breakfast, lunch, dinner, snack
    
    private LocalDateTime mealTime;
    
    @DecimalMin(value = "0.01", message = "食用量必须大于0")
    private BigDecimal amount;
    
    private String unit; // g, ml, 份, 个
    
    private String notes; // 备注
    
    private String imageUrl; // 照片URL
}
