package com.health.agent.module.food.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建食物DTO
 */
@Data
public class FoodCreateDTO {
    
    @NotBlank(message = "食物名称不能为空")
    private String name;
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    private String brand;
    
    private String barcode;
    
    @NotBlank(message = "单位不能为空")
    private String unit;
    
    @NotNull(message = "每份大小不能为空")
    private BigDecimal servingSize;
    
    @NotNull(message = "热量不能为空")
    private BigDecimal calories;
    
    @NotNull(message = "蛋白质不能为空")
    private BigDecimal protein;
    
    @NotNull(message = "碳水化合物不能为空")
    private BigDecimal carbohydrate;
    
    @NotNull(message = "脂肪不能为空")
    private BigDecimal fat;
    
    private BigDecimal fiber;
    
    private BigDecimal sodium;
    
    private BigDecimal sugar;
    
    private BigDecimal vitaminA;
    
    private BigDecimal vitaminC;
    
    private BigDecimal calcium;
    
    private BigDecimal iron;
    
    private String imageUrl;
    
    private String description;
    
    private String source;
}
