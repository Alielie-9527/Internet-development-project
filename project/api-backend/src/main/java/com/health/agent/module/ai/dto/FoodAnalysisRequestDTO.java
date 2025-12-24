package com.health.agent.module.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 食物图片分析请求DTO
 * 
 * @author Health Agent Team
 * @date 2025-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "食物图片分析请求")
public class FoodAnalysisRequestDTO {
    
    @NotBlank(message = "图片数据不能为空")
    @Schema(description = "Base64编码的图片数据", required = true)
    private String base64Image;
    
    @Schema(description = "用户ID（可选）")
    private Long userId;
}
