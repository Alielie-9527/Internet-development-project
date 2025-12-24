package com.health.agent.module.diet.controller;

import com.health.agent.common.api.ApiResponse;
import com.health.agent.module.diet.dto.AddDietDiaryDTO;
import com.health.agent.module.diet.dto.DietDiaryQueryDTO;
import com.health.agent.module.diet.dto.UpdateDietDiaryDTO;
import com.health.agent.module.diet.service.IDietDiaryService;
import com.health.agent.module.diet.vo.DietDiaryVO;
import com.health.agent.module.diet.vo.DietStatisticsVO;
import com.health.agent.module.user.entity.User;
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
 * 饮食日记控制器
 */
@Tag(name = "饮食日记管理")
@RestController
@RequestMapping("/api/diet/diary")
@RequiredArgsConstructor
public class DietDiaryController {
    
    private final IDietDiaryService dietDiaryService;
    
    @Operation(summary = "添加饮食日记")
    @PostMapping("/add")
    public ApiResponse<Long> addDietDiary(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddDietDiaryDTO dto) {
        Long id = dietDiaryService.addDietDiary(user.getId(), dto);
        return ApiResponse.ok(id);
    }
    
    @Operation(summary = "更新饮食日记")
    @PutMapping("/update")
    public ApiResponse<Void> updateDietDiary(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateDietDiaryDTO dto) {
        dietDiaryService.updateDietDiary(user.getId(), dto);
        return ApiResponse.ok();
    }
    
    @Operation(summary = "删除饮食日记")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteDietDiary(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        dietDiaryService.deleteDietDiary(user.getId(), id);
        return ApiResponse.ok();
    }
    
    @Operation(summary = "查询饮食日记详情")
    @GetMapping("/detail/{id}")
    public ApiResponse<DietDiaryVO> getDietDiaryById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        DietDiaryVO vo = dietDiaryService.getDietDiaryById(user.getId(), id);
        return ApiResponse.ok(vo);
    }
    
    @Operation(summary = "查询饮食日记列表")
    @PostMapping("/list")
    public ApiResponse<List<DietDiaryVO>> getDietDiaryList(
            @AuthenticationPrincipal User user,
            @RequestBody DietDiaryQueryDTO query) {
        List<DietDiaryVO> list = dietDiaryService.getDietDiaryList(user.getId(), query);
        return ApiResponse.ok(list);
    }
    
    @Operation(summary = "查询某日期的饮食记录")
    @GetMapping("/date/{date}")
    public ApiResponse<List<DietDiaryVO>> getDietDiaryByDate(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<DietDiaryVO> list = dietDiaryService.getDietDiaryByDate(user.getId(), date);
        return ApiResponse.ok(list);
    }
    
    @Operation(summary = "查询日期范围的饮食记录")
    @GetMapping("/dateRange")
    public ApiResponse<List<DietDiaryVO>> getDietDiaryByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DietDiaryVO> list = dietDiaryService.getDietDiaryByDateRange(user.getId(), startDate, endDate);
        return ApiResponse.ok(list);
    }
    
    @Operation(summary = "获取某日期的饮食统计")
    @GetMapping("/statistics/{date}")
    public ApiResponse<DietStatisticsVO> getDietStatistics(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DietStatisticsVO statistics = dietDiaryService.getDietStatistics(user.getId(), date);
        return ApiResponse.ok(statistics);
    }
    
    @Operation(summary = "获取今日饮食统计")
    @GetMapping("/statistics/today")
    public ApiResponse<DietStatisticsVO> getTodayStatistics(
            @AuthenticationPrincipal User user) {
        DietStatisticsVO statistics = dietDiaryService.getDietStatistics(user.getId(), LocalDate.now());
        return ApiResponse.ok(statistics);
    }
}

