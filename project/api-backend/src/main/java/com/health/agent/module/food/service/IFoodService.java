package com.health.agent.module.food.service;

import com.health.agent.module.food.dto.FoodCreateDTO;
import com.health.agent.module.food.dto.FoodSearchDTO;
import com.health.agent.module.food.vo.FoodVO;

import java.util.List;
import java.util.Map;

/**
 * 食物服务接口
 */
public interface IFoodService {
    
    /**
     * 根据ID获取食物详情
     */
    FoodVO getFoodById(Long id);
    
    /**
     * 根据条形码获取食物
     */
    FoodVO getFoodByBarcode(String barcode);
    
    /**
     * 搜索食物
     */
    Map<String, Object> searchFood(FoodSearchDTO dto);
    
    /**
     * 根据分类获取食物列表
     */
    List<FoodVO> getFoodByCategory(String category);
    
    /**
     * 获取所有食物分类统计
     */
    Map<String, Long> getCategoryStats();
    
    /**
     * 创建食物
     */
    FoodVO createFood(FoodCreateDTO dto);
    
    /**
     * 更新食物
     */
    FoodVO updateFood(Long id, FoodCreateDTO dto);
    
    /**
     * 删除食物
     */
    void deleteFood(Long id);
}
