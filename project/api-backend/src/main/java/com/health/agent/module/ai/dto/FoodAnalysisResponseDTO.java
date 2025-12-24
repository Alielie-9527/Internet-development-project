package com.health.agent.module.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 食物图片分析响应DTO
 * 
 * @author Health Agent Team
 * @date 2025-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "食物图片分析响应")
public class FoodAnalysisResponseDTO {
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "食物信息")
    private FoodInfo food;
    
    @Schema(description = "错误消息（如果失败）")
    private String errorMessage;
    
    @Schema(description = "营养分析文本")
    private String nutritionAnalysis;
    
    @Schema(description = "图片URL（如果已保存）")
    private String imageUrl;
    
    /**
     * 食物信息详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "食物信息详情")
    public static class FoodInfo {
        
        @Schema(description = "食物ID（如果在数据库中找到）")
        private Long id;
        
        @Schema(description = "食物名称")
        private String name;
        
        @Schema(description = "食物分类")
        private String category;
        
        @Schema(description = "热量（千卡/100g）")
        private Double calories;
        
        @Schema(description = "蛋白质（克/100g）")
        private Double protein;
        
        @Schema(description = "脂肪（克/100g）")
        private Double fat;
        
        @Schema(description = "碳水化合物（克/100g）")
        private Double carbohydrate;
        
        @Schema(description = "单位")
        private String unit;
        
        @Schema(description = "建议分量")
        private String suggestedPortion;
        
        @Schema(description = "健康建议")
        private String advice;
    }
}
