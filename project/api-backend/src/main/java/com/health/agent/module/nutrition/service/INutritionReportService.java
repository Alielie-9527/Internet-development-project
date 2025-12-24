package com.health.agent.module.nutrition.service;

import com.health.agent.module.nutrition.dto.GenerateReportDTO;
import com.health.agent.module.nutrition.dto.ReportQueryDTO;
import com.health.agent.module.nutrition.vo.NutritionReportVO;
import com.health.agent.module.nutrition.vo.NutritionTrendVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 营养报告服务接口
 */
public interface INutritionReportService {
    
    /**
     * 生成营养报告
     */
    Long generateReport(Long userId, GenerateReportDTO dto);
    
    /**
     * 根据ID查询报告
     */
    NutritionReportVO getReportById(Long userId, Long id);
    
    /**
     * 查询报告列表
     */
    List<NutritionReportVO> getReportList(Long userId, ReportQueryDTO query);
    
    /**
     * 删除报告
     */
    void deleteReport(Long userId, Long id);
    
    /**
     * 查询最新报告
     */
    NutritionReportVO getLatestReport(Long userId, String reportType);
    
    /**
     * 查询营养趋势
     */
    NutritionTrendVO getNutritionTrend(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 重新生成AI建议
     */
    void regenerateAIAdvice(Long userId, Long reportId);
}
