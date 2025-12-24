package com.health.agent.module.diet.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 饮食日记VO
 */
@Data
public class DietDiaryVO {
    
    private Long id;
    
    private Long userId;
    
    private Long foodId;
    
    private String foodName; // 食物名称
    
    private String foodCategory; // 食物分类
    
    private String mealType; // breakfast-早餐 lunch-午餐 dinner-晚餐 snack-加餐
    
    private String mealTypeDesc; // 用餐类型描述
    
    private LocalDateTime mealTime;
    
    private BigDecimal amount; // 食用量
    
    private String unit; // 单位
    
    private BigDecimal calories; // 热量(kcal)
    
    private BigDecimal protein; // 蛋白质(g)
    
    private BigDecimal carbohydrate; // 碳水化合物(g)
    
    private BigDecimal fat; // 脂肪(g)
    
    private String notes; // 备注
    
    private String imageUrl; // 照片
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
