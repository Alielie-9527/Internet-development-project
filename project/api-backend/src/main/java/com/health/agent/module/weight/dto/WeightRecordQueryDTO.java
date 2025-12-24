package com.health.agent.module.weight.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 体重记录查询DTO
 */
@Data
public class WeightRecordQueryDTO {
    
    private LocalDate startDate; // 开始日期
    
    private LocalDate endDate; // 结束日期
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 30;
}
