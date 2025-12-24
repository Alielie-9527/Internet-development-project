package com.health.agent.module.diet.mapper;

import com.health.agent.module.diet.entity.DietDiary;
import com.health.agent.module.diet.dto.DietDiaryQueryDTO;
import com.health.agent.module.diet.vo.DietDiaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食日记Mapper
 */
@Mapper
public interface DietDiaryMapper {
    
    /**
     * 插入饮食日记
     */
    int insert(DietDiary dietDiary);
    
    /**
     * 根据ID查询
     */
    DietDiary selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询VO
     */
    DietDiaryVO selectVOById(@Param("id") Long id);
    
    /**
     * 更新饮食日记
     */
    int updateById(DietDiary dietDiary);
    
    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 条件查询列表
     */
    List<DietDiaryVO> selectList(@Param("userId") Long userId, @Param("query") DietDiaryQueryDTO query);
    
    /**
     * 查询用户某日期的饮食记录
     */
    List<DietDiaryVO> selectByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    /**
     * 查询用户日期范围内的饮食记录
     */
    List<DietDiaryVO> selectByUserAndDateRange(@Param("userId") Long userId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * 统计用户某日期的营养摄入
     */
    DietDiaryVO sumNutritionByDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
