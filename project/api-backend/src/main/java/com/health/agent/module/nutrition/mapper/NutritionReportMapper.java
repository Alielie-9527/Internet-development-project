package com.health.agent.module.nutrition.mapper;

import com.health.agent.module.nutrition.entity.NutritionReport;
import com.health.agent.module.nutrition.dto.ReportQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 营养报告Mapper
 */
@Mapper
public interface NutritionReportMapper {
    
    /**
     * 插入报告
     */
    int insert(NutritionReport report);
    
    /**
     * 根据ID查询
     */
    NutritionReport selectById(@Param("id") Long id);
    
    /**
     * 更新报告
     */
    int updateById(NutritionReport report);
    
    /**
     * 删除报告
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 条件查询列表
     */
    List<NutritionReport> selectList(@Param("userId") Long userId, @Param("query") ReportQueryDTO query);
    
    /**
     * 查询用户指定日期和类型的报告
     */
    NutritionReport selectByUserDateType(@Param("userId") Long userId, 
                                         @Param("reportDate") LocalDate reportDate, 
                                         @Param("reportType") String reportType);
    
    /**
     * 查询用户日期范围内的报告
     */
    List<NutritionReport> selectByDateRange(@Param("userId") Long userId, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
}
