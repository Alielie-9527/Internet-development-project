package com.health.agent.module.nutrition.controller;

import com.health.agent.common.api.ApiResponse;
import com.health.agent.module.nutrition.dto.GenerateReportDTO;
import com.health.agent.module.nutrition.dto.ReportQueryDTO;
import com.health.agent.module.nutrition.service.INutritionReportService;
import com.health.agent.module.nutrition.vo.NutritionReportVO;
import com.health.agent.module.nutrition.vo.NutritionTrendVO;
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
 * 营养报告控制器
 */
@Tag(name = "营养分析")
@RestController
@RequestMapping("/api/nutrition/report")
@RequiredArgsConstructor
public class NutritionReportController {

    private final INutritionReportService nutritionReportService;

    @Operation(summary = "生成营养报告（含可选AI建议）")
    @PostMapping("/generate")
    public ApiResponse<Long> generateReport(@AuthenticationPrincipal User user, @Valid @RequestBody GenerateReportDTO dto) {
        Long id = nutritionReportService.generateReport(user.getId(), dto);
        return ApiResponse.ok(id);
    }

    @Operation(summary = "获取报告详情")
    @GetMapping("/{id}")
    public ApiResponse<NutritionReportVO> getReport(@AuthenticationPrincipal User user, @PathVariable Long id) {
        NutritionReportVO vo = nutritionReportService.getReportById(user.getId(), id);
        return ApiResponse.ok(vo);
    }

    @Operation(summary = "查询报告列表")
    @PostMapping("/list")
    public ApiResponse<List<NutritionReportVO>> list(@AuthenticationPrincipal User user, @RequestBody ReportQueryDTO query) {
        List<NutritionReportVO> list = nutritionReportService.getReportList(user.getId(), query);
        return ApiResponse.ok(list);
    }

    @Operation(summary = "删除报告")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        nutritionReportService.deleteReport(user.getId(), id);
        return ApiResponse.ok();
    }

    @Operation(summary = "获取最新报告")
    @GetMapping("/latest")
    public ApiResponse<NutritionReportVO> latest(@AuthenticationPrincipal User user, @RequestParam String reportType) {
        NutritionReportVO vo = nutritionReportService.getLatestReport(user.getId(), reportType);
        return ApiResponse.ok(vo);
    }

    @Operation(summary = "营养趋势")
    @GetMapping("/trend")
    public ApiResponse<NutritionTrendVO> trend(@AuthenticationPrincipal User user,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        NutritionTrendVO vm = nutritionReportService.getNutritionTrend(user.getId(), startDate, endDate);
        return ApiResponse.ok(vm);
    }

    @Operation(summary = "重新生成 AI 建议")
    @PostMapping("/regenerate/{id}")
    public ApiResponse<Void> regenerate(@AuthenticationPrincipal User user, @PathVariable Long id) {
        nutritionReportService.regenerateAIAdvice(user.getId(), id);
        return ApiResponse.ok();
    }
}

