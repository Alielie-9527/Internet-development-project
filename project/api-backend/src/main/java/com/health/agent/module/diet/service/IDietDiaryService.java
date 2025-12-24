package com.health.agent.module.diet.service;

import com.health.agent.module.diet.dto.AddDietDiaryDTO;
import com.health.agent.module.diet.dto.DietDiaryQueryDTO;
import com.health.agent.module.diet.dto.UpdateDietDiaryDTO;
import com.health.agent.module.diet.vo.DietDiaryVO;
import com.health.agent.module.diet.vo.DietStatisticsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食日记服务接口
 */
public interface IDietDiaryService {
    
    /**
     * 添加饮食日记
     */
    Long addDietDiary(Long userId, AddDietDiaryDTO dto);
    
    /**
     * 更新饮食日记
     */
    void updateDietDiary(Long userId, UpdateDietDiaryDTO dto);
    
    /**
     * 删除饮食日记
     */
    void deleteDietDiary(Long userId, Long id);
    
    /**
     * 根据ID查询
     */
    DietDiaryVO getDietDiaryById(Long userId, Long id);
    
    /**
     * 查询饮食日记列表
     */
    List<DietDiaryVO> getDietDiaryList(Long userId, DietDiaryQueryDTO query);
    
    /**
     * 查询某日期的饮食记录
     */
    List<DietDiaryVO> getDietDiaryByDate(Long userId, LocalDate date);
    
    /**
     * 查询日期范围的饮食记录
     */
    List<DietDiaryVO> getDietDiaryByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取某日期的饮食统计
     */
    DietStatisticsVO getDietStatistics(Long userId, LocalDate date);
}
