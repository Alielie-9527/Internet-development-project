package com.health.agent.module.weight.controller;

import com.health.agent.common.api.ApiResponse;
import com.health.agent.module.user.entity.User;
import com.health.agent.module.weight.dto.AddWeightRecordDTO;
import com.health.agent.module.weight.dto.UpdateWeightRecordDTO;
import com.health.agent.module.weight.dto.WeightRecordQueryDTO;
import com.health.agent.module.weight.service.IWeightRecordService;
import com.health.agent.module.weight.vo.WeightRecordVO;
import com.health.agent.module.weight.vo.WeightStatisticsVO;
import com.health.agent.module.weight.vo.WeightTrendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 体重管理控制器
 */
@Tag(name = "体重管理")
@RestController
@RequestMapping("/api/weight")
@RequiredArgsConstructor
public class WeightRecordController {
    
    private final IWeightRecordService weightRecordService;
    
    @Operation(summary = "添加体重记录")
    @PostMapping("/add")
    public ApiResponse<Long> addWeightRecord(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddWeightRecordDTO dto) {
        Long id = weightRecordService.addWeightRecord(user.getId(), dto);
        return ApiResponse.ok(id);
    }
    
    @Operation(summary = "更新体重记录")
    @PutMapping("/update")
    public ApiResponse<Void> updateWeightRecord(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateWeightRecordDTO dto) {
        weightRecordService.updateWeightRecord(user.getId(), dto);
        return ApiResponse.ok();
    }
    
    @Operation(summary = "删除体重记录")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteWeightRecord(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        weightRecordService.deleteWeightRecord(user.getId(), id);
        return ApiResponse.ok();
    }
    
    @Operation(summary = "查询体重记录详情")
    @GetMapping("/detail/{id}")
    public ApiResponse<WeightRecordVO> getWeightRecordById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        WeightRecordVO vo = weightRecordService.getWeightRecordById(user.getId(), id);
        return ApiResponse.ok(vo);
    }
    
    @Operation(summary = "查询体重记录列表")
    @PostMapping("/list")
    public ApiResponse<List<WeightRecordVO>> getWeightRecordList(
            @AuthenticationPrincipal User user,
            @RequestBody WeightRecordQueryDTO query) {
        List<WeightRecordVO> list = weightRecordService.getWeightRecordList(user.getId(), query);
        return ApiResponse.ok(list);
    }
    
    @Operation(summary = "获取最新体重记录")
    @GetMapping("/latest")
    public ApiResponse<WeightRecordVO> getLatestWeightRecord(
            @AuthenticationPrincipal User user) {
        WeightRecordVO vo = weightRecordService.getLatestWeightRecord(user.getId());
        return ApiResponse.ok(vo);
    }
    
    @Operation(summary = "获取体重趋势图表数据")
    @GetMapping("/trend")
    public ApiResponse<WeightTrendVO> getWeightTrend(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        WeightTrendVO trend = weightRecordService.getWeightTrend(user.getId(), startDate, endDate);
        return ApiResponse.ok(trend);
    }
    
    @Operation(summary = "获取体重统计")
    @GetMapping("/statistics")
    public ApiResponse<WeightStatisticsVO> getWeightStatistics(
            @AuthenticationPrincipal User user) {
        WeightStatisticsVO statistics = weightRecordService.getWeightStatistics(user.getId());
        return ApiResponse.ok(statistics);
    }
    
    @Operation(summary = "获取最近30天体重趋势")
    @GetMapping("/trend/last30days")
    public ApiResponse<WeightTrendVO> getLast30DaysTrend(
            @AuthenticationPrincipal User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        WeightTrendVO trend = weightRecordService.getWeightTrend(user.getId(), startDate, endDate);
        return ApiResponse.ok(trend);
    }
    
    @Operation(summary = "获取最近90天体重趋势")
    @GetMapping("/trend/last90days")
    public ApiResponse<WeightTrendVO> getLast90DaysTrend(
            @AuthenticationPrincipal User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(90);
        WeightTrendVO trend = weightRecordService.getWeightTrend(user.getId(), startDate, endDate);
        return ApiResponse.ok(trend);
    }
}

