package com.health.agent.module.food.controller;

import com.health.agent.common.api.ApiResponse;
import com.health.agent.module.food.dto.FoodCreateDTO;
import com.health.agent.module.food.dto.FoodSearchDTO;
import com.health.agent.module.food.service.IFoodService;
import com.health.agent.module.food.vo.FoodVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 食物控制器
 */
@Tag(name = "食物管理", description = "食物库的增删改查、搜索等接口")
@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {

    private final IFoodService foodService;

    @Operation(summary = "根据ID获取食物详情")
    @GetMapping("/{id}")
    public ApiResponse<FoodVO> getFoodById(
            @Parameter(description = "食物ID") @PathVariable Long id) {
        FoodVO food = foodService.getFoodById(id);
        return ApiResponse.ok(food);
    }

    @Operation(summary = "根据条形码获取食物")
    @GetMapping("/barcode/{barcode}")
    public ApiResponse<FoodVO> getFoodByBarcode(
            @Parameter(description = "食物条形码") @PathVariable String barcode) {
        FoodVO food = foodService.getFoodByBarcode(barcode);
        return ApiResponse.ok(food);
    }

    @Operation(summary = "搜索食物", description = "支持按名称、分类、条形码搜索，支持分页")
    @PostMapping("/search")
    public ApiResponse<Map<String, Object>> searchFood(
            @RequestBody FoodSearchDTO dto) {
        Map<String, Object> result = foodService.searchFood(dto);
        return ApiResponse.ok(result);
    }

    @Operation(summary = "根据分类获取食物列表")
    @GetMapping("/category/{category}")
    public ApiResponse<List<FoodVO>> getFoodByCategory(
            @Parameter(description = "分类：staple-主食 vegetable-蔬菜 fruit-水果 protein-蛋白质 dairy-乳制品 snack-零食 drink-饮品") 
            @PathVariable String category) {
        List<FoodVO> list = foodService.getFoodByCategory(category);
        return ApiResponse.ok(list);
    }

    @Operation(summary = "获取食物分类统计")
    @GetMapping("/category/stats")
    public ApiResponse<Map<String, Long>> getCategoryStats() {
        Map<String, Long> stats = foodService.getCategoryStats();
        return ApiResponse.ok(stats);
    }

    @Operation(summary = "创建食物", description = "用户可自定义添加食物到食物库")
    @PostMapping
    public ApiResponse<FoodVO> createFood(
            @Validated @RequestBody FoodCreateDTO dto) {
        FoodVO food = foodService.createFood(dto);
        return ApiResponse.ok("创建成功", food);
    }

    @Operation(summary = "更新食物")
    @PutMapping("/{id}")
    public ApiResponse<FoodVO> updateFood(
            @Parameter(description = "食物ID") @PathVariable Long id,
            @Validated @RequestBody FoodCreateDTO dto) {
        FoodVO food = foodService.updateFood(id, dto);
        return ApiResponse.ok("更新成功", food);
    }

    @Operation(summary = "删除食物")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteFood(
            @Parameter(description = "食物ID") @PathVariable Long id) {
        foodService.deleteFood(id);
        return ApiResponse.ok("删除成功");
    }
}
