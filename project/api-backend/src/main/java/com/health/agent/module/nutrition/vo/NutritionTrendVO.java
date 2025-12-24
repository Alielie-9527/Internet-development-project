package com.health.agent.module.nutrition.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 营养趋势VO
 */
@Data
@Builder
public class NutritionTrendVO {
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal avgCalories; // 平均热量
    
    private BigDecimal avgProtein; // 平均蛋白质
    
    private BigDecimal avgCarbohydrate; // 平均碳水
    
    private BigDecimal avgFat; // 平均脂肪
    
    private BigDecimal avgScore; // 平均评分
    
    private Integer reportCount; // 报告数量
}
