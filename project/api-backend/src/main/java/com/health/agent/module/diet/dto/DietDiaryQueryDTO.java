package com.health.agent.module.diet.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 饮食日记查询DTO
 */
@Data
public class DietDiaryQueryDTO {
    
    private String mealType; // breakfast, lunch, dinner, snack
    
    private LocalDate startDate; // 开始日期
    
    private LocalDate endDate; // 结束日期
    
    private Long foodId; // 食物ID
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 20;
}
