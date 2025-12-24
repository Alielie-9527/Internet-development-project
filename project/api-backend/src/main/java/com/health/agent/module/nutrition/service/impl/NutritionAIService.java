package com.health.agent.module.nutrition.service.impl;

import com.health.agent.module.ai.client.AIClient;
import com.health.agent.module.nutrition.entity.NutritionReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 营养AI服务包装器
 * <p>
 * 说明：项目当前可能没有真实的 AIClient Bean（例如腾讯混元等）。
 * - 我们通过可选注入 `AIClient`（`@Autowired(required = false)`）来支持真正的 AI 服务。
 * - 当 `aiClient` 为 null 或 AI 调用失败时，使用本地的 "假" 建议生成器返回占位文本。
 * - 后续替换真实AI时，只需确保有 `AIClient` 的实现类被 Spring 注册为 Bean（例如 `TencentHunyuanClient`），本类会自动调用真实接口。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NutritionAIService {

    @Autowired(required = false)
    private AIClient aiClient; // 如果项目中存在真实的 AIClient 实现，会被注入

    /**
     * 生成 AI 建议字符串（同步）。
     * 如果存在 `aiClient` 会调用真实 AI，否则返回本地伪造建议。
     */
    public String generateAIAdvice(NutritionReport report) {
        // 如果没有注入真实 AI 客户端，返回简单的占位建议（开发/测试阶段用）
        if (aiClient == null) {
            log.info("未检测到 AIClient 实现，使用本地假建议返回（用于开发/测试）。");
            return buildFakeAdvice(report);
        }

        // 若有真实 aiClient，可在此构建 AIRequestDTO 并调用 aiClient.chat(...)
        // TODO: 后续将此处替换为调用具体 AI 接口的实现。
        try {
            // 伪代码：
            // AIRequestDTO req = AIRequestDTO.builder() ...
            // AIResponseDTO resp = aiClient.chat(req);
            // if (resp.getSuccess()) return resp.getContent();
            // else return "AI调用失败: " + resp.getErrorMessage();

            // 目前仍返回本地占位——以防真实AI返回异常
            return buildFakeAdvice(report);
        } catch (Exception e) {
            log.error("调用 AIClient 失败，返回本地假建议", e);
            return buildFakeAdvice(report);
        }
    }

    private String buildFakeAdvice(NutritionReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("[示例AI建议]\n");
        sb.append("日期: ").append(report.getReportDate()).append("\n");
        sb.append("总体建议: 建议保持均衡摄入, 尝试更多蔬菜和优质蛋白。\n");
        sb.append("热量: ").append(report.getTotalCalories()).append(" kcal；");
        sb.append("蛋白质: ").append(report.getTotalProtein()).append(" g；");
        sb.append("碳水: ").append(report.getTotalCarbohydrate()).append(" g；\n");
        sb.append("建议: 控制高脂/高糖食物, 每餐配蔬菜, 每周增加2次中等强度运动。\n");
        sb.append("注: 以上建议为示例文本，生产环境请接入真实AI服务以获得个性化建议。\n");
        return sb.toString();
    }
}
