package com.health.agent.module.weight.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 体重趋势图表VO
 */
@Data
@Builder
public class WeightTrendVO {
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal currentWeight; // 当前体重
    
    private BigDecimal startWeight; // 起始体重
    
    private BigDecimal minWeight; // 最小体重
    
    private BigDecimal maxWeight; // 最大体重
    
    private BigDecimal avgWeight; // 平均体重
    
    private BigDecimal totalChange; // 总变化量
    
    private BigDecimal changeRate; // 变化率(%)
    
    private Integer recordCount; // 记录次数
    
    private List<DataPoint> dataPoints; // 数据点列表
    
    @Data
    @Builder
    public static class DataPoint {
        private LocalDate date;
        private BigDecimal weight;
        private BigDecimal bmi;
        private BigDecimal bodyFatRate;
    }
}
