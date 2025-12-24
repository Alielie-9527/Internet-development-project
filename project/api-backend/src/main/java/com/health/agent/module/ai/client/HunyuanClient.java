package com.health.agent.module.ai.client;

import com.health.agent.module.ai.dto.AIRequestDTO;
import com.health.agent.module.ai.dto.AIResponseDTO;
import com.health.agent.module.ai.dto.FoodAnalysisRequestDTO;
import com.health.agent.module.ai.dto.FoodAnalysisResponseDTO;

/**
 * 已弃用的混元客户端占位类
 * 保留此类以避免历史依赖，但不再实现实际调用逻辑。
 * 
 * @deprecated 请使用 {@link TencentHunyuanClient} 代替
 */
@Deprecated
public class HunyuanClient implements AIClient {

    @Override
    public AIResponseDTO chat(AIRequestDTO request) {
        throw new UnsupportedOperationException("HunyuanClient 已弃用，请使用 TencentHunyuanClient");
    }

    @Override
    public FoodAnalysisResponseDTO analyzeFoodImage(FoodAnalysisRequestDTO request) {
        throw new UnsupportedOperationException("HunyuanClient 已弃用，请使用 TencentHunyuanClient");
    }
}



