package com.health.agent.module.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食物实体类
 */
@Data
public class Food {
    
    private Long id;
    
    private String name;
    
    private String category; // staple-主食 vegetable-蔬菜 fruit-水果 protein-蛋白质 dairy-乳制品 snack-零食 drink-饮品
    
    private String brand;
    
    private String barcode;
    
    private String unit; // g-克 ml-毫升 个 份
    
    private BigDecimal servingSize; // 每份大小
    
    private BigDecimal calories; // 热量(kcal)
    
    private BigDecimal protein; // 蛋白质(g)
    
    private BigDecimal carbohydrate; // 碳水化合物(g)
    
    private BigDecimal fat; // 脂肪(g)
    
    private BigDecimal fiber; // 膳食纤维(g)
    
    private BigDecimal sodium; // 钠(mg)
    
    private BigDecimal sugar; // 糖(g)
    
    private BigDecimal vitaminA; // 维生素A(μg)
    
    private BigDecimal vitaminC; // 维生素C(mg)
    
    private BigDecimal calcium; // 钙(mg)
    
    private BigDecimal iron; // 铁(mg)
    
    private String imageUrl;
    
    private String description;
    
    private Integer isVerified; // 是否官方认证
    
    private String source;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Integer isDeleted;
}
