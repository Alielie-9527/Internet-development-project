package com.health.agent.module.weight.service;

import com.health.agent.module.weight.dto.AddWeightRecordDTO;
import com.health.agent.module.weight.dto.UpdateWeightRecordDTO;
import com.health.agent.module.weight.dto.WeightRecordQueryDTO;
import com.health.agent.module.weight.vo.WeightRecordVO;
import com.health.agent.module.weight.vo.WeightStatisticsVO;
import com.health.agent.module.weight.vo.WeightTrendVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 体重记录服务接口
 */
public interface IWeightRecordService {
    
    /**
     * 添加体重记录
     */
    Long addWeightRecord(Long userId, AddWeightRecordDTO dto);
    
    /**
     * 更新体重记录
     */
    void updateWeightRecord(Long userId, UpdateWeightRecordDTO dto);
    
    /**
     * 删除体重记录
     */
    void deleteWeightRecord(Long userId, Long id);
    
    /**
     * 根据ID查询
     */
    WeightRecordVO getWeightRecordById(Long userId, Long id);
    
    /**
     * 查询体重记录列表
     */
    List<WeightRecordVO> getWeightRecordList(Long userId, WeightRecordQueryDTO query);
    
    /**
     * 获取最新体重记录
     */
    WeightRecordVO getLatestWeightRecord(Long userId);
    
    /**
     * 获取体重趋势
     */
    WeightTrendVO getWeightTrend(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取体重统计
     */
    WeightStatisticsVO getWeightStatistics(Long userId);
}
