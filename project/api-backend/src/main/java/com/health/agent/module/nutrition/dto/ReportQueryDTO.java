package com.health.agent.module.nutrition.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 营养报告查询DTO
 */
@Data
public class ReportQueryDTO {
    
    private String reportType; // 兼容旧版本: daily, weekly, monthly
    
    private String reportPeriod; // 新版本: WEEK, MONTH, QUARTER
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer page = 1; // 兼容前端的page字段
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 20;
}
