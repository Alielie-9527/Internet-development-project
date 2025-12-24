package com.health.agent.module.nutrition.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 生成营养报告DTO
 */
@Data
public class GenerateReportDTO {
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    @NotNull(message = "报告周期不能为空")
    private String reportPeriod; // WEEK, MONTH, QUARTER
    
    private Boolean useAI = true; // 是否生成AI建议（兼容前端的useAI字段）
    
    // 兼容旧字段
    private LocalDate reportDate; // 兼容旧版本，如果没有startDate/endDate则使用此字段
    private String reportType; // 兼容旧版本 daily, weekly, monthly
    private Boolean withAIAdvice; // 兼容旧版本
}
