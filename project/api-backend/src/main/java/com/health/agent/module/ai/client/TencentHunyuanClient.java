package com.health.agent.module.ai.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.agent.config.AIConfig;
import com.health.agent.module.ai.dto.AIRequestDTO;
import com.health.agent.module.ai.dto.AIResponseDTO;
import com.health.agent.module.ai.dto.FoodAnalysisRequestDTO;
import com.health.agent.module.ai.dto.FoodAnalysisResponseDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 腾讯混元大模型HTTP客户端实现
 * 使用原生HTTP请求，直接调用 qwen 兼容的 HTTP 接口
 * 
 * @author Health Agent Team
 * @since 2025-01-01
 */
@Slf4j
@Component
@Primary
public class TencentHunyuanClient implements AIClient {
    
    private final AIConfig aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // 明确允许的模型常量
    private static final String TEXT_MODEL = "qwen-max";
    private static final String VISION_MODEL = "qwen-vl-max-latest";
    
    public TencentHunyuanClient(AIConfig aiConfig) {
        this.aiConfig = aiConfig;
        this.restTemplate = createRestTemplate();
        this.objectMapper = new ObjectMapper();
        
        log.info("========== 腾讯混元HTTP客户端初始化 ==========");
        log.info("API URL: {}", aiConfig.getApiUrl());
        log.info("API Key 长度: {}", aiConfig.getApiKey() != null ? aiConfig.getApiKey().length() : 0);
        log.info("Model: {}", aiConfig.getModel());
        
        // 验证API Key
        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().trim().isEmpty()) {
            log.error("❌ 错误：API Key 未配置！AI功能将无法使用");
        } else if (aiConfig.getApiKey().startsWith("${AI_API_KEY")) {
            log.error("❌ 错误：API Key 未正确替换，仍然是占位符格式");
        } else {
            log.info("✅ API Key 配置正常");
        }
        
        // 校验并修正配置，后端只支持固定的两个模型
        if (aiConfig.getModel() == null || !TEXT_MODEL.equals(aiConfig.getModel())) {
            log.warn("配置的AI文本模型 '{}' 非受支持模型，强制设置为 '{}'", aiConfig.getModel(), TEXT_MODEL);
            aiConfig.setModel(TEXT_MODEL);
        }
        
        log.info("✅ QWENHTTP客户端初始化成功");
        log.info("=============================================");
    }
    
    private RestTemplate createRestTemplate() {
        // 使用SimpleClientHttpRequestFactory配置超时
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30秒连接超时
        factory.setReadTimeout(60000);    // 60秒读取超时
        
        RestTemplate template = new RestTemplate(factory);
        log.info("RestTemplate配置完成 - 连接超时: 30s, 读取超时: 60s");
        return template;
    }
    
    @Override
    public AIResponseDTO chat(AIRequestDTO request) {
        try {
            log.info("========== 腾讯混元API请求详情 ==========");
            log.info("API URL: {}", aiConfig.getApiUrl());
            log.info("Model: {}", aiConfig.getModel());
            log.info("Max Tokens: {}", aiConfig.getMaxTokens());
            log.info("Temperature: {}", aiConfig.getTemperature());
            log.info("消息数量: {}", request.getMessages().size());
            
            // 构建请求体
            HunyuanRequest hunyuanRequest = new HunyuanRequest();
            // 文本对话请求统一使用后端支持的文本模型
            hunyuanRequest.setModel(TEXT_MODEL);
            hunyuanRequest.setMaxTokens(aiConfig.getMaxTokens());
            hunyuanRequest.setTemperature(aiConfig.getTemperature());
            hunyuanRequest.setStream(false);
            
            // 转换消息格式
            List<HunyuanMessage> messages = request.getMessages().stream()
                    .map(msg -> {
                        HunyuanMessage hunyuanMsg = new HunyuanMessage();
                        hunyuanMsg.setRole(msg.getRole());
                        hunyuanMsg.setContent(msg.getContent());
                        return hunyuanMsg;
                    })
                    .collect(Collectors.toList());
            hunyuanRequest.setMessages(messages);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());
            
            // 创建请求实体
            HttpEntity<HunyuanRequest> entity = new HttpEntity<>(hunyuanRequest, headers);
            
            log.info("发送请求到: {}/chat/completions", aiConfig.getApiUrl());
            long startTime = System.currentTimeMillis();
            
            // 发送HTTP请求
            ResponseEntity<HunyuanResponse> response = restTemplate.exchange(
                    aiConfig.getApiUrl() + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    HunyuanResponse.class
            );
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("请求完成，耗时: {}ms", elapsed);
            
            // 处理响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                HunyuanResponse hunyuanResponse = response.getBody();
                
                if (hunyuanResponse.getChoices() != null && !hunyuanResponse.getChoices().isEmpty()) {
                    String content = extractContent(hunyuanResponse.getChoices().get(0).getMessage().getContent());
                    
                    // 构建使用情况
                    AIResponseDTO.Usage usage = null;
                    if (hunyuanResponse.getUsage() != null) {
                        usage = AIResponseDTO.Usage.builder()
                                .promptTokens(hunyuanResponse.getUsage().getPromptTokens())
                                .completionTokens(hunyuanResponse.getUsage().getCompletionTokens())
                                .totalTokens(hunyuanResponse.getUsage().getTotalTokens())
                                .build();
                    }
                    
                    log.info("AI响应成功 - Tokens: {}, Reply length: {}", 
                            usage != null ? usage.getTotalTokens() : 0, content.length());
                    
                    return AIResponseDTO.builder()
                            .content(content)
                            .model(aiConfig.getModel())
                            .usage(usage)
                            .success(true)
                            .build();
                } else {
                    throw new RuntimeException("响应中没有有效的选择项");
                }
            } else {
                throw new RuntimeException("HTTP请求失败，状态码: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("========== 腾讯混元API请求失败 ==========");
            log.error("错误类型: {}", e.getClass().getName());
            log.error("错误信息: {}", e.getMessage());
            log.error("API URL: {}", aiConfig.getApiUrl());
            log.error("Model: {}", aiConfig.getModel());
            log.error("完整堆栈:", e);
            
            throw new RuntimeException("腾讯混元API调用失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void chatStream(AIRequestDTO request, StreamCallback callback) {
        // 流式实现可以后续添加
        throw new UnsupportedOperationException("腾讯混元流式模式暂未实现");
    }
    
    @Override
    public FoodAnalysisResponseDTO analyzeFoodImage(FoodAnalysisRequestDTO request) {
        try {
            log.info("========== 开始食物图片分析 ==========");
            log.info("用户ID: {}", request.getUserId());
            
            // 第一步：使用 qwen-vl-max-latest 分析图片
            log.info("第一步：使用 qwen-vl-max-latest 分析图片");
            String imageAnalysisText = analyzeImageWithVisionModel(request.getBase64Image());
            log.info("图片分析结果: {}", imageAnalysisText);
            
            // 第二步：使用 qwen-max 进行结构化处理
            log.info("第二步：使用 qwen-max 进行结构化处理");
            StructuredAnalysisResult result = structureFoodAnalysis(imageAnalysisText);
            
            log.info("食物分析完成 - 名称: {}", result.foodInfo.getName());
            
            return FoodAnalysisResponseDTO.builder()
                    .success(true)
                    .food(result.foodInfo)
                    .nutritionAnalysis(result.nutritionAnalysis)
                    .build();
                    
        } catch (Exception e) {
            log.error("========== 食物图片分析失败 ==========");
            log.error("错误类型: {}", e.getClass().getName());
            log.error("错误信息: {}", e.getMessage());
            log.error("完整堆栈:", e);
            
            return FoodAnalysisResponseDTO.builder()
                    .success(false)
                    .errorMessage("食物图片分析失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 使用视觉模型分析图片
     */
    private String analyzeImageWithVisionModel(String base64Image) {
        try {
            // 构建标准OpenAI兼容格式的视觉模型请求
            HunyuanRequest visionRequest = new HunyuanRequest();
            visionRequest.setModel(VISION_MODEL);
            visionRequest.setMaxTokens(2000);
            visionRequest.setTemperature(0.7);
            visionRequest.setStream(false);
            
            // 构建消息 - qwen-vl使用标准的messages格式,content是数组
            List<HunyuanMessage> messages = new ArrayList<>();
            HunyuanMessage userMessage = new HunyuanMessage();
            userMessage.setRole("user");
            
            // 构建多模态content数组
            List<MultimodalContent> contentList = new ArrayList<>();
            
            // 添加文本内容
            MultimodalContent textContent = new MultimodalContent();
            textContent.setType("text");
            textContent.setText("请仔细分析这张食物图片，识别图片中的食物并提供以下详细信息：\n" +
                "1. 食物名称（中文）\n" +
                "2. 食物分类（如：主食、肉类、蔬菜、水果、饮品等）\n" +
                "3. 每100克的营养成分：热量（千卡）、蛋白质（克）、脂肪（克）、碳水化合物（克）\n" +
                "4. 常用单位和建议分量\n" +
                "5. 健康建议\n\n" +
                "请提供准确的数值估计。");
            contentList.add(textContent);
            
            // 添加图片内容
            MultimodalContent imageContent = new MultimodalContent();
            imageContent.setType("image_url");
            ImageUrl imgUrl = new ImageUrl();
            imgUrl.setUrl("data:image/jpeg;base64," + base64Image);
            imageContent.setImageUrl(imgUrl);
            contentList.add(imageContent);
            
            userMessage.setContent(contentList);
            messages.add(userMessage);
            
            visionRequest.setMessages(messages);
            
            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());
            
            HttpEntity<HunyuanRequest> entity = new HttpEntity<>(visionRequest, headers);
            
            log.info("发送视觉模型请求到: {}/chat/completions", aiConfig.getApiUrl());
            log.info("使用模型: {}", VISION_MODEL);
            
            ResponseEntity<HunyuanResponse> response = restTemplate.exchange(
                    aiConfig.getApiUrl() + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    HunyuanResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                HunyuanResponse visionResponse = response.getBody();
                if (visionResponse.getChoices() != null && !visionResponse.getChoices().isEmpty()) {
                    String content = extractContent(visionResponse.getChoices().get(0).getMessage().getContent());
                    log.info("视觉模型分析结果长度: {}", content.length());
                    return content;
                } else {
                    throw new RuntimeException("视觉模型返回数据格式不正确");
                }
            } else {
                throw new RuntimeException("视觉模型请求失败，状态码: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("视觉模型分析失败", e);
            throw new RuntimeException("图片分析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 结构化分析结果（内部类）
     */
    @Data
    private static class StructuredAnalysisResult {
        private FoodAnalysisResponseDTO.FoodInfo foodInfo;
        private String nutritionAnalysis;
    }
    
    /**
     * 使用文本模型进行结构化处理
     */
    private StructuredAnalysisResult structureFoodAnalysis(String analysisText) {
        try {
            // 构建标准OpenAI格式请求
            StructuredModelRequest structuredRequest = new StructuredModelRequest();
            structuredRequest.setModel(TEXT_MODEL);
            structuredRequest.setMaxTokens(2000);
            structuredRequest.setTemperature(0.3);  // 更低的温度以获得更准确的结果
            structuredRequest.setStream(false);
            
            // 构建消息
            List<HunyuanMessage> messages = new ArrayList<>();
            
            // 系统消息
            HunyuanMessage systemMessage = new HunyuanMessage();
            systemMessage.setRole("system");
            systemMessage.setContent("""
                    你是一个营养分析专家。请将食物分析结果转换为严格的JSON格式，包含以下字段：
                    {
                        "food": {
                            "name": "食物名称（字符串）",
                            "category": "食物分类（字符串，如：主食、肉类、蔬菜、水果）",
                            "calories": 数值（每100克热量，千卡，必须是数字）,
                            "protein": 数值（每100克蛋白质，克，必须是数字）,
                            "fat": 数值（每100克脂肪，克，必须是数字）,
                            "carbohydrate": 数值（每100克碳水化合物，克，必须是数字）,
                            "unit": "常用单位（如：克、个、碗、杯）",
                            "suggestedPortion": "建议分量（如：100克、1个、1碗）",
                            "advice": "健康建议（字符串）"
                        },
                        "nutritionAnalysis": "详细的营养分析文本（字符串）"
                    }
                    
                    重要：calories、protein、fat、carbohydrate必须是数字类型，不要加单位。
                    如果无法准确识别某项数值，请给出合理估计值。
                    只返回JSON，不要添加其他文字说明。
                    """);
            messages.add(systemMessage);
            
            // 用户消息
            HunyuanMessage userMessage = new HunyuanMessage();
            userMessage.setRole("user");
            userMessage.setContent(analysisText);
            messages.add(userMessage);
            
            structuredRequest.setMessages(messages);
            
            // 设置响应格式为JSON
            ResponseFormat responseFormat = new ResponseFormat();
            responseFormat.setType("json_object");
            structuredRequest.setResponseFormat(responseFormat);
            
            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApiKey());
            
            HttpEntity<StructuredModelRequest> entity = new HttpEntity<>(structuredRequest, headers);
            
            log.info("发送结构化处理请求到: {}/chat/completions", aiConfig.getApiUrl());
            ResponseEntity<HunyuanResponse> response = restTemplate.exchange(
                    aiConfig.getApiUrl() + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    HunyuanResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                HunyuanResponse structuredResponse = response.getBody();
                if (structuredResponse.getChoices() != null && !structuredResponse.getChoices().isEmpty()) {
                    // 解析JSON
                    String jsonText = extractContent(structuredResponse.getChoices().get(0).getMessage().getContent());
                    log.info("结构化JSON响应: {}", jsonText);
                    
                    Map<String, Object> jsonMap = objectMapper.readValue(jsonText, Map.class);
                    Map<String, Object> foodData = (Map<String, Object>) jsonMap.get("food");
                    
                    // 安全地转换数值类型
                    Double calories = convertToDouble(foodData.get("calories"));
                    Double protein = convertToDouble(foodData.get("protein"));
                    Double fat = convertToDouble(foodData.get("fat"));
                    Double carbohydrate = convertToDouble(foodData.get("carbohydrate"));
                    
                    FoodAnalysisResponseDTO.FoodInfo foodInfo = FoodAnalysisResponseDTO.FoodInfo.builder()
                            .name((String) foodData.get("name"))
                            .category((String) foodData.get("category"))
                            .calories(calories)
                            .protein(protein)
                            .fat(fat)
                            .carbohydrate(carbohydrate)
                            .unit((String) foodData.get("unit"))
                            .suggestedPortion((String) foodData.get("suggestedPortion"))
                            .advice((String) foodData.get("advice"))
                            .build();
                    
                    String nutritionAnalysis = (String) jsonMap.get("nutritionAnalysis");
                    
                    StructuredAnalysisResult result = new StructuredAnalysisResult();
                    result.setFoodInfo(foodInfo);
                    result.setNutritionAnalysis(nutritionAnalysis);
                    
                    return result;
                } else {
                    throw new RuntimeException("结构化处理返回数据格式不正确");
                }
            } else {
                throw new RuntimeException("结构化处理请求失败，状态码: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("结构化处理失败", e);
            throw new RuntimeException("结构化处理失败: " + e.getMessage(), e);
        }
    }
    
    // 腾讯混元API请求格式
    @Data
    public static class HunyuanRequest {
        private String model;
        private List<HunyuanMessage> messages;
        
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        
        private Double temperature;
        private Boolean stream;
    }
    
    @Data
    public static class HunyuanMessage {
        private String role;
        private Object content;  // 改为Object类型以支持String或List
    }
    
    /**
     * 多模态内容项(用于视觉模型)
     */
    @Data
    public static class MultimodalContent {
        private String type;  // "text" 或 "image_url"
        private String text;  // type=text时使用
        
        @JsonProperty("image_url")
        private ImageUrl imageUrl;  // type=image_url时使用
    }
    
    @Data
    public static class ImageUrl {
        private String url;  // data:image/jpeg;base64,xxx
    }
    
    // 腾讯混元API响应格式
    @Data
    public static class HunyuanResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<HunyuanChoice> choices;
        private HunyuanUsage usage;
    }
    
    @Data
    public static class HunyuanChoice {
        private Integer index;
        private HunyuanMessage message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
    }
    
    @Data
    public static class HunyuanUsage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
    
    // ========== 视觉模型相关数据结构 ==========
    
    /**
     * API请求包装器（模拟前端格式）
     */
    @Data
    public static class ApiRequestWrapper {
        private String apiKey;
        private Object data;
    }
    
    /**
     * 视觉模型请求
     */
    @Data
    public static class VisionModelRequest {
        private String model;
        private VisionInput input;
    }
    
    /**
     * 视觉输入
     */
    @Data
    public static class VisionInput {
        private List<VisionMessage> messages;
    }
    
    /**
     * 视觉消息
     */
    @Data
    public static class VisionMessage {
        private String role;
        private List<VisionContent> content;
    }
    
    /**
     * 视觉内容（支持图片和文本）
     */
    @Data
    public static class VisionContent {
        private String image;  // base64编码的图片
        private String text;   // 文本内容
    }
    
    /**
     * 视觉模型响应
     */
    @Data
    public static class VisionModelResponse {
        private VisionOutput output;
    }
    
    /**
     * 视觉输出
     */
    @Data
    public static class VisionOutput {
        private String text;
    }
    
    // ========== 结构化模型相关数据结构 ==========
    
    /**
     * 结构化模型请求（标准OpenAI格式）
     */
    @Data
    public static class StructuredModelRequest {
        private String model;
        private List<HunyuanMessage> messages;
        
        @JsonProperty("response_format")
        private ResponseFormat responseFormat;
        
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        
        private Double temperature;
        private Boolean stream;
    }
    
    /**
     * 响应格式
     */
    @Data
    public static class ResponseFormat {
        private String type;  // "json_object"
    }
    
    /**
     * 结构化模型响应
     */
    @Data
    public static class StructuredModelResponse {
        private StructuredOutput output;
    }
    
    /**
     * 结构化输出
     */
    @Data
    public static class StructuredOutput {
        private String text;  // JSON字符串
    }
    
    /**
     * 安全地从HunyuanMessage中提取文本内容
     * 支持String格式和多模态数组格式
     */
    private String extractContent(Object content) {
        if (content == null) {
            return "";
        }
        
        // 如果是String,直接返回
        if (content instanceof String) {
            return (String) content;
        }
        
        // 如果是List(多模态格式),提取text类型的内容
        if (content instanceof List) {
            List<?> contentList = (List<?>) content;
            for (Object item : contentList) {
                if (item instanceof Map) {
                    Map<?, ?> contentItem = (Map<?, ?>) item;
                    if ("text".equals(contentItem.get("type"))) {
                        return String.valueOf(contentItem.get("text"));
                    }
                }
            }
        }
        
        // 其他情况,转为字符串
        return String.valueOf(content);
    }
    
    /**
     * 安全地将Object转换为Double
     */
    private Double convertToDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                // 移除可能的单位和空格
                String str = ((String) value).replaceAll("[^0-9.]", "");
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                log.warn("无法转换字符串为数字: {}", value);
                return 0.0;
            }
        }
        return 0.0;
    }
}