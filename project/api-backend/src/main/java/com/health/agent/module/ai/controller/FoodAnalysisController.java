package com.health.agent.module.ai.controller;

import com.health.agent.common.api.ApiResponse;
import com.health.agent.module.ai.client.AIClient;
import com.health.agent.module.ai.dto.FoodAnalysisRequestDTO;
import com.health.agent.module.ai.dto.FoodAnalysisResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Iterator;

/**
 * 食物图片分析控制器
 * 提供AI图片分析功能
 * 
 * @author Health Agent Team
 * @date 2025-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI食物分析接口", description = "使用AI分析食物图片")
@Validated
public class FoodAnalysisController {

    @Autowired
    private AIClient aiClient;

    /**
     * 分析食物图片（接收文件上传）
     */
    @PostMapping(value = "/analyze-food/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "分析食物图片（文件上传）", description = "上传食物图片文件，自动转为base64后进行AI分析")
    public ApiResponse<FoodAnalysisResponseDTO> analyzeFoodImageUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId) {
        log.info("========== 开始处理食物图片上传请求 ==========");
        log.info("用户ID: {}, 文件名: {}, 文件大小: {} bytes", userId, file.getOriginalFilename(), file.getSize());
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ApiResponse.fail("上传的文件为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.fail("只支持图片文件（jpg、png、jpeg等）");
            }
            
            // 验证文件大小（最大10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ApiResponse.fail("图片文件不能超过10MB");
            }
            
            // 转换为Base64并压缩
            byte[] bytes = file.getBytes();
            byte[] compressedBytes = compressImage(bytes);
            String base64Image = Base64.getEncoder().encodeToString(compressedBytes);
            
            // 检查Base64长度(qwen-vl限制约130000字符)
            if (base64Image.length() > 120000) {
                log.warn("压缩后图片仍然过大: {} 字符, 进行二次压缩", base64Image.length());
                compressedBytes = compressImage(compressedBytes, 0.3);
                base64Image = Base64.getEncoder().encodeToString(compressedBytes);
            }
            
            log.info("图片Base64长度: {}", base64Image.length());
            
            // 构建请求
            FoodAnalysisRequestDTO request = FoodAnalysisRequestDTO.builder()
                    .base64Image(base64Image)
                    .userId(userId)
                    .build();
            
            // 调用AI分析
            FoodAnalysisResponseDTO response = aiClient.analyzeFoodImage(request);
            
            if (Boolean.TRUE.equals(response.getSuccess())) {
                log.info("食物图片分析成功 - 食物名称: {}", response.getFood().getName());
                return ApiResponse.ok("分析成功", response);
            } else {
                log.error("食物图片分析失败: {}", response.getErrorMessage());
                return ApiResponse.fail("分析失败: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("========== 文件上传转换异常 ==========", e);
            return ApiResponse.fail("分析失败: " + e.getMessage());
        }
    }

    /**
     * 分析食物图片（接收base64）
     */
    @PostMapping("/analyze-food")
    @Operation(summary = "分析食物图片（Base64）", description = "直接传入base64编码的图片数据进行分析")
    public ApiResponse<FoodAnalysisResponseDTO> analyzeFoodImage(@Valid @RequestBody FoodAnalysisRequestDTO request) {
        log.info("========== 开始处理食物图片分析请求 ==========");
        log.info("用户ID: {}", request.getUserId());
        log.info("图片数据长度: {}", request.getBase64Image() != null ? request.getBase64Image().length() : 0);
        
        try {
            // 验证图片数据
            if (request.getBase64Image() == null || request.getBase64Image().trim().isEmpty()) {
                log.warn("图片数据为空");
                return ApiResponse.fail("图片数据不能为空");
            }
            
            // 调用AI客户端进行分析
            FoodAnalysisResponseDTO response = aiClient.analyzeFoodImage(request);
            
            if (Boolean.TRUE.equals(response.getSuccess())) {
                log.info("食物图片分析成功 - 食物名称: {}", response.getFood().getName());
                return ApiResponse.ok("分析成功", response);
            } else {
                log.error("食物图片分析失败: {}", response.getErrorMessage());
                return ApiResponse.fail("分析失败: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("========== 食物图片分析异常 ==========", e);
            return ApiResponse.fail("分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 压缩图片
     * @param imageBytes 原始图片字节
     * @return 压缩后的图片字节
     */
    private byte[] compressImage(byte[] imageBytes) throws Exception {
        return compressImage(imageBytes, 0.5);
    }
    
    /**
     * 压缩图片
     * @param imageBytes 原始图片字节
     * @param quality 压缩质量 0.0-1.0
     * @return 压缩后的图片字节
     */
    private byte[] compressImage(byte[] imageBytes, double quality) throws Exception {
        try {
            // 读取图片
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage originalImage = ImageIO.read(bais);
            
            if (originalImage == null) {
                throw new RuntimeException("无法读取图片");
            }
            
            // 计算压缩后的尺寸(最大800px)
            int maxDimension = 800;
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int newWidth = originalWidth;
            int newHeight = originalHeight;
            
            if (originalWidth > maxDimension || originalHeight > maxDimension) {
                double scale = Math.min(
                    (double) maxDimension / originalWidth,
                    (double) maxDimension / originalHeight
                );
                newWidth = (int) (originalWidth * scale);
                newHeight = (int) (originalHeight * scale);
            }
            
            // 缩放图片
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();
            
            // 压缩质量
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                throw new RuntimeException("没有找到JPEG编码器");
            }
            
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality((float) quality);
            
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(resizedImage, null, null), param);
            
            writer.dispose();
            ios.close();
            
            byte[] compressed = baos.toByteArray();
            log.info("图片压缩: {}x{} -> {}x{}, {}KB -> {}KB, 质量: {}", 
                originalWidth, originalHeight, newWidth, newHeight,
                imageBytes.length / 1024, compressed.length / 1024, quality);
            
            return compressed;
            
        } catch (Exception e) {
            log.error("图片压缩失败", e);
            throw new RuntimeException("图片压缩失败: " + e.getMessage());
        }
    }
}
