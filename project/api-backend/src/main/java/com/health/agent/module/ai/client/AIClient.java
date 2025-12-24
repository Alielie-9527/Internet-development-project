package com.health.agent.module.ai.client;

import com.health.agent.module.ai.dto.AIRequestDTO;
import com.health.agent.module.ai.dto.AIResponseDTO;
import com.health.agent.module.ai.dto.FoodAnalysisRequestDTO;
import com.health.agent.module.ai.dto.FoodAnalysisResponseDTO;

/**
 * AI客户端接口
 * 定义AI模型调用的统一接口，支持不同的AI提供商实现
 * 
 * @author Health Agent Team
 * @date 2025-01-01
 */
public interface AIClient {
    
    /**
     * 发送对话请求并获取AI回复
     * 
     * @param request AI请求对象，包含消息内容和配置参数
     * @return AI响应对象，包含回复内容和使用情况
     */
    AIResponseDTO chat(AIRequestDTO request);
    
    /**
     * 发送对话请求并获取流式回复（可选实现）
     * 
     * @param request AI请求对象
     * @param callback 流式回复的回调函数
     */
    default void chatStream(AIRequestDTO request, StreamCallback callback) {
        throw new UnsupportedOperationException("Stream mode not supported");
    }
    
    /**
     * 分析食物图片
     * 使用多模态模型分析食物图片，并返回结构化的食物信息
     * 
     * @param request 包含base64编码图片的请求对象
     * @return 食物分析结果，包含名称、营养成分、卡路里和健康建议
     */
    FoodAnalysisResponseDTO analyzeFoodImage(FoodAnalysisRequestDTO request);
    
    /**
     * 流式回复回调接口
     */
    @FunctionalInterface
    interface StreamCallback {
        /**
         * 接收AI返回的文本片段
         * 
         * @param content 文本片段
         */
        void onMessage(String content);
        
        /**
         * 流式传输完成
         */
        default void onComplete() {}
        
        /**
         * 发生错误
         * 
         * @param error 错误信息
         */
        default void onError(Throwable error) {}
    }
}

