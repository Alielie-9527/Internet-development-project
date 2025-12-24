package com.health.agent.module.food.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食物视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodVO {
    
    private Long id;
    
    private String name;
    
    private String category;
    
    private String categoryName; // 分类中文名
    
    private String brand;
    
    private String barcode;
    
    private String unit;
    
    private BigDecimal servingSize;
    
    private BigDecimal calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbohydrate;
    
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
    
    private Boolean isVerified;
    
    private String source;
    
    private LocalDateTime createdAt;
}
