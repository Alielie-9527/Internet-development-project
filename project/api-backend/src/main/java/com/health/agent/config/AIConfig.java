package com.health.agent.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务配置
 * 使用原生HTTP客户端，当前后端仅支持通义千问(qwen)相关模型
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {

    /**
     * AI服务提供商（固定为 qwen）
     */
    private String provider = "qwen";

    /**
     * API密钥（用于访问 qwen 兼容的代理或服务）
     */
    private String apiKey;
    
    /**
     * 配置加载后的验证
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("========== AI配置加载情况 ==========");
        log.info("Provider: {}", provider);
        log.info("API URL: {}", apiUrl);
        log.info("Model: {}", model);
        log.info("API Key: {}...{}", 
            apiKey != null && apiKey.length() > 8 ? apiKey.substring(0, 8) : "未设置",
            apiKey != null && apiKey.length() > 8 ? apiKey.substring(apiKey.length() - 4) : "");
        log.info("Max Tokens: {}", maxTokens);
        log.info("Temperature: {}", temperature);
        log.info("Timeout: {}ms", timeout);
        
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("${AI_API_KEY")) {
            log.error("⚠️  警告：AI API Key 未正确配置！请检查 application.yml 中的 ai.api-key 配置");
        } else {
            log.info("✅ AI配置加载成功");
        }
        log.info("======================================");
    }

    /**
     * API基础URL（示例：qwen 兼容代理或服务地址）
     * 例如: https://dashscope.aliyuncs.com/compatible-mode/v1
     */
    private String apiUrl;

    /**
     * 模型名称（后端仅支持两种模型）
     * - 文本模型: "qwen-max"
     * - 视觉模型: "qwen-vl-max-latest"
     */
    private String model = "qwen-max";

    /** 请求超时时间（毫秒） */
    private Long timeout = 60000L;

    /** 最大生成Token数 */
    private Integer maxTokens = 2000;

    /** 温度参数（0-2） 越高越随机，越低越确定 */
    private Double temperature = 0.7;

    /**
     * 获取系统提示词（饮食健康智能顾问）
     */
    public String getSystemPrompt() {
        return """
                你是一位专业的饮食健康智能顾问，致力于帮助用户建立科学的饮食习惯和健康的生活方式。
                
                核心职责：
                1. 提供科学、权威的营养知识和饮食建议
                2. 帮助用户制定个性化的饮食计划和健康目标
                3. 解答关于食物营养、热量、营养搭配的问题
                4. 指导健康的体重管理和饮食调理方法
                5. 推荐营养均衡、美味健康的食谱
                6. 分析用户的饮食结构，指出潜在问题
                
                对话风格：
                - 专业、友善、耐心、平易近人
                - 用通俗易懂的语言解释营养学知识
                - 提供具体、可操作、因地制宜的建议
                - 鼓励并支持用户养成健康习惯
                - 关注用户的实际情况和可行性
                
                注意事项：
                - 不做医学诊断，不替代医生或专业营养师的建议
                - 遇到严重健康问题或疾病相关问题，建议咨询医生
                - 尊重个体差异、饮食文化和个人偏好
                - 强调均衡营养、适量原则和循序渐进
                - 避免极端饮食方案，注重可持续性
                
                请用专业、温暖的方式与用户对话，帮助他们实现健康饮食目标，建立长期健康的生活方式。
                """;
    }
}