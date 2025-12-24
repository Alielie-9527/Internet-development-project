package com.health.agent.module.food.mapper;

import com.health.agent.module.food.entity.Food;
import com.health.agent.module.food.dto.FoodSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 食物Mapper接口
 */
@Mapper
public interface FoodMapper {
    
    /**
     * 根据ID查询食物
     */
    Food findById(@Param("id") Long id);
    
    /**
     * 根据ID查询食物(MyBatis-Plus风格)
     */
    Food selectById(@Param("id") Long id);
    
    /**
     * 根据名称精确查询食物
     */
    Food findByName(@Param("name") String name);
    
    /**
     * 根据条形码查询食物
     */
    Food findByBarcode(@Param("barcode") String barcode);
    
    /**
     * 搜索食物（支持分页和多条件筛选）
     */
    List<Food> search(@Param("dto") FoodSearchDTO dto);
    
    /**
     * 统计搜索结果数量
     */
    int countSearch(@Param("dto") FoodSearchDTO dto);
    
    /**
     * 根据分类查询食物列表
     */
    List<Food> findByCategory(@Param("category") String category);
    
    /**
     * 查询所有食物（分页）
     */
    List<Food> findAll(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 统计食物总数
     */
    int count();
    
    /**
     * 插入食物
     */
    int insert(Food food);
    
    /**
     * 更新食物
     */
    int update(Food food);
    
    /**
     * 删除食物（逻辑删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量插入食物
     */
    int batchInsert(@Param("list") List<Food> foodList);
}
