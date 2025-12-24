package com.health.agent.module.diet.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 饮食统计VO
 */
@Data
public class DietStatisticsVO {
    
    private LocalDate date; // 日期
    
    private BigDecimal totalCalories; // 总热量
    
    private BigDecimal totalProtein; // 总蛋白质
    
    private BigDecimal totalCarbohydrate; // 总碳水化合物
    
    private BigDecimal totalFat; // 总脂肪
    
    private Integer mealCount; // 进餐次数
    
    private List<MealSummary> meals; // 各餐汇总
    
    @Data
    public static class MealSummary {
        private String mealType;
        private String mealTypeDesc;
        private BigDecimal calories;
        private BigDecimal protein;
        private BigDecimal carbohydrate;
        private BigDecimal fat;
        private Integer itemCount;
    }
}
