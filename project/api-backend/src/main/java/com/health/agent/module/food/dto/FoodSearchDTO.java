package com.health.agent.module.food.dto;

import lombok.Data;

/**
 * 食物搜索DTO
 */
@Data
public class FoodSearchDTO {
    
    private String keyword; // 搜索关键词（食物名称）
    
    private String category; // 分类筛选
    
    private String barcode; // 条形码搜索
    
    private Integer isVerified; // 是否只显示认证食物
    
    private Integer pageNum = 1; // 页码
    
    private Integer pageSize = 20; // 每页数量
}
