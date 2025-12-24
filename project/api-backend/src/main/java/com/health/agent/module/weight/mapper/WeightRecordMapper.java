package com.health.agent.module.weight.mapper;

import com.health.agent.module.weight.entity.WeightRecord;
import com.health.agent.module.weight.dto.WeightRecordQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 体重记录Mapper
 */
@Mapper
public interface WeightRecordMapper {
    
    /**
     * 插入体重记录
     */
    int insert(WeightRecord record);
    
    /**
     * 根据ID查询
     */
    WeightRecord selectById(@Param("id") Long id);
    
    /**
     * 更新体重记录
     */
    int updateById(WeightRecord record);
    
    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 条件查询列表
     */
    List<WeightRecord> selectList(@Param("userId") Long userId, @Param("query") WeightRecordQueryDTO query);
    
    /**
     * 查询用户最新的一条记录
     */
    WeightRecord selectLatestByUser(@Param("userId") Long userId);
    
    /**
     * 查询用户指定日期的记录
     */
    WeightRecord selectByUserAndDate(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);
    
    /**
     * 查询用户日期范围内的记录
     */
    List<WeightRecord> selectByDateRange(@Param("userId") Long userId, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * 查询用户某日期之前最近的一条记录
     */
    WeightRecord selectPreviousRecord(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);
    
    /**
     * 统计用户记录总数
     */
    int countByUser(@Param("userId") Long userId);
}
