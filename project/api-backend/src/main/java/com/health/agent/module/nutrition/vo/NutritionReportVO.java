package com.health.agent.module.nutrition.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 营养报告VO
 */
@Data
public class NutritionReportVO {
    
    private Long id;
    
    private Long userId;
    
    private LocalDate reportDate;
    
    private String reportType; // daily-每日 weekly-每周 monthly-每月
    
    private String reportPeriod; // 报告周期 WEEK, MONTH, QUARTER (兼容前端)
    
    private String reportTypeDesc; // 报告类型描述
    
    private BigDecimal avgCalories; // 平均热量(兼容前端字段)
    private BigDecimal avgProtein; // 平均蛋白质
    private BigDecimal avgCarbohydrate; // 平均碳水
    private BigDecimal avgFat; // 平均脂肪
    
    private Integer compliantDays; // 达标天数(兼容前端字段)
    
    private Integer overallScore; // 综合评分(兼容前端字段)
    
    private BigDecimal totalCalories; // 总热量
    
    private BigDecimal totalProtein; // 总蛋白质(g)
    
    private BigDecimal totalCarbohydrate; // 总碳水化合物(g)
    
    private BigDecimal totalFat; // 总脂肪(g)
    
    private BigDecimal caloriesBurned; // 消耗热量
    
    private BigDecimal netCalories; // 净热量
    
    private BigDecimal goalCompletionRate; // 目标完成率(%)
    
    private Integer nutritionBalanceScore; // 营养均衡评分(0-100)
    
    private String nutritionGrade; // 营养等级 A-优秀 B-良好 C-及格 D-需改进
    
    private String aiAdvice; // AI建议
    
    private NutritionDetail nutritionDetail; // 营养详情

    // 体重相关（基于报告时间范围动态计算，不一定持久化）
    private java.math.BigDecimal startWeight; // 报告期起始体重(kg)
    private java.math.BigDecimal endWeight; // 报告期结束/当前体重(kg)
    private java.math.BigDecimal weightChange; // 体重变化(kg)
    private java.math.BigDecimal weightChangeRate; // 变化率(%)
    
    private LocalDateTime createdAt;

    // 报告时间范围（便于前端请求趋势数据）
    private LocalDate startDate;
    private LocalDate endDate;

    private Integer totalDays; // 报告期天数
    
    // 目标营养值（用于前端显示）
    private BigDecimal targetCalories; // 目标热量
    private BigDecimal targetProtein; // 目标蛋白质
    private BigDecimal targetFat; // 目标脂肪
    private BigDecimal targetCarbohydrate; // 目标碳水
    
    @Data
    public static class NutritionDetail {
        private BigDecimal proteinRatio; // 蛋白质占比(%)
        private BigDecimal carbRatio; // 碳水占比(%)
        private BigDecimal fatRatio; // 脂肪占比(%)
        
        private BigDecimal recommendCalories; // 推荐热量
        private BigDecimal recommendProtein; // 推荐蛋白质
        private BigDecimal recommendCarb; // 推荐碳水
        private BigDecimal recommendFat; // 推荐脂肪
        
        private String caloriesStatus; // 热量状态 low-偏低 normal-正常 high-偏高
        private String proteinStatus; // 蛋白质状态
        private String carbStatus; // 碳水状态
        private String fatStatus; // 脂肪状态
    }
}
