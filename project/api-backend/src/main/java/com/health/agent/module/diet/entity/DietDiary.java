package com.health.agent.module.diet.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 饮食日记实体类
 */
@Data
public class DietDiary {
    
    private Long id;
    
    private Long userId;
    
    private Long foodId;
    
    private String foodName; // 冗余保存的食物名称
    
    private String mealType; // breakfast-早餐 lunch-午餐 dinner-晚餐 snack-加餐
    
    private LocalDateTime mealTime;
    
    private BigDecimal amount; // 食用量
    
    private String unit; // 单位: g, ml, 份, 个
    
    private BigDecimal calories; // 热量(kcal)
    
    private BigDecimal protein; // 蛋白质(g)
    
    private BigDecimal carbohydrate; // 碳水化合物(g)
    
    private BigDecimal fat; // 脂肪(g)
    
    private String notes; // 备注
    
    private String imageUrl; // 照片
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
