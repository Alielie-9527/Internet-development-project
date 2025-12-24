package com.health.agent.module.nutrition.service.impl;

import com.health.agent.common.exception.BusinessException;
import com.health.agent.module.diet.mapper.DietDiaryMapper;
import com.health.agent.module.nutrition.dto.GenerateReportDTO;
import com.health.agent.module.nutrition.dto.ReportQueryDTO;
import com.health.agent.module.nutrition.entity.NutritionReport;
import com.health.agent.module.nutrition.mapper.NutritionReportMapper;
import com.health.agent.module.nutrition.service.INutritionReportService;
import com.health.agent.module.nutrition.vo.NutritionReportVO;
import com.health.agent.module.nutrition.vo.NutritionTrendVO;
import com.health.agent.module.diet.vo.DietDiaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 营养报告服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionReportServiceImpl implements INutritionReportService {

    private final NutritionReportMapper nutritionReportMapper;
    private final DietDiaryMapper dietDiaryMapper;
    private final NutritionAIService nutritionAIService;
    private final com.health.agent.module.weight.service.IWeightRecordService weightRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateReport(Long userId, GenerateReportDTO dto) {
        // 兼容旧版本参数
        LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : dto.getReportDate();
        LocalDate endDate = dto.getEndDate() != null ? dto.getEndDate() : dto.getReportDate();
        String reportPeriod = dto.getReportPeriod() != null ? dto.getReportPeriod() : dto.getReportType();
        Boolean useAI = dto.getUseAI() != null ? dto.getUseAI() : (dto.getWithAIAdvice() != null ? dto.getWithAIAdvice() : true);
        
        if (startDate == null || endDate == null) {
            throw new BusinessException("开始日期和结束日期不能为空");
        }
        if (reportPeriod == null) {
            throw new BusinessException("报告周期不能为空");
        }

        // 统计日期范围内的营养摄入
        List<DietDiaryVO> diaryList = dietDiaryMapper.selectByUserAndDateRange(userId, startDate, endDate);
        
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarb = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        int daysWithData = 0;
        
        if (diaryList != null && !diaryList.isEmpty()) {
            for (DietDiaryVO diary : diaryList) {
                if (diary.getCalories() != null) totalCalories = totalCalories.add(diary.getCalories());
                if (diary.getProtein() != null) totalProtein = totalProtein.add(diary.getProtein());
                if (diary.getCarbohydrate() != null) totalCarb = totalCarb.add(diary.getCarbohydrate());
                if (diary.getFat() != null) totalFat = totalFat.add(diary.getFat());
                daysWithData++;
            }
        }
        
        // 计算平均值
        int totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal avgCalories = daysWithData > 0 ? totalCalories.divide(BigDecimal.valueOf(daysWithData), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgProtein = daysWithData > 0 ? totalProtein.divide(BigDecimal.valueOf(daysWithData), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgCarb = daysWithData > 0 ? totalCarb.divide(BigDecimal.valueOf(daysWithData), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgFat = daysWithData > 0 ? totalFat.divide(BigDecimal.valueOf(daysWithData), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 计算综合评分（简单算法：基于营养均衡度）
        Integer nutritionScore = calculateNutritionScore(avgCalories, avgProtein, avgCarb, avgFat);
        
        // 获取用户目标热量（从user表或默认值）
        BigDecimal targetCalories = getUserTargetCalories(userId);
        BigDecimal caloriesTolerance = targetCalories.multiply(BigDecimal.valueOf(0.1)); // 10%容差
        BigDecimal minCalories = targetCalories.subtract(caloriesTolerance);
        BigDecimal maxCalories = targetCalories.add(caloriesTolerance);
        
        // 计算达标天数（基于用户目标热量±10%）
        int compliantDays = 0;
        if (diaryList != null) {
            for (DietDiaryVO diary : diaryList) {
                BigDecimal cal = diary.getCalories() != null ? diary.getCalories() : BigDecimal.ZERO;
                if (cal.compareTo(minCalories) >= 0 && cal.compareTo(maxCalories) <= 0) {
                    compliantDays++;
                }
            }
        }

        NutritionReport report = new NutritionReport();
        report.setUserId(userId);
        report.setReportDate(endDate); // 使用结束日期作为报告日期
        report.setReportType(reportPeriod);
        report.setTotalCalories(avgCalories.setScale(2, RoundingMode.HALF_UP));
        report.setTotalProtein(avgProtein.setScale(2, RoundingMode.HALF_UP));
        report.setTotalCarbohydrate(avgCarb.setScale(2, RoundingMode.HALF_UP));
        report.setTotalFat(avgFat.setScale(2, RoundingMode.HALF_UP));
        report.setCaloriesBurned(BigDecimal.ZERO);
        report.setNetCalories(avgCalories);
        report.setGoalCompletionRate(totalDays > 0 ? BigDecimal.valueOf(compliantDays * 100.0 / totalDays).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        report.setNutritionBalanceScore(nutritionScore);
        report.setCreatedAt(LocalDateTime.now());

        nutritionReportMapper.insert(report);

        // 生成 AI 建议（可选）
        if (useAI) {
            try {
                String advice = nutritionAIService.generateAIAdvice(report);
                report.setAiAdvice(advice);
                nutritionReportMapper.updateById(report);
            } catch (Exception e) {
                log.warn("生成AI建议失败: {}", e.getMessage());
                // AI失败不影响报告生成
            }
        }

        return report.getId();
    }
    
    /**
     * 获取用户目标热量
     */
    private BigDecimal getUserTargetCalories(Long userId) {
        // 尝试从用户设置中获取目标热量，如果没有则使用默认值2000
        try {
            // 这里可以添加查询user表的target_calories字段的逻辑
            // User user = userMapper.selectById(userId);
            // if (user.getTargetCalories() != null) return user.getTargetCalories();
            return BigDecimal.valueOf(2000); // 默认值
        } catch (Exception e) {
            return BigDecimal.valueOf(2000);
        }
    }
    
    /**
     * 计算营养评分
     */
    private Integer calculateNutritionScore(BigDecimal avgCalories, BigDecimal avgProtein, BigDecimal avgCarb, BigDecimal avgFat) {
        int score = 60; // 基础分
        
        // 热量合理性 (1800-2200为最佳) +20分
        if (avgCalories.compareTo(BigDecimal.valueOf(1800)) >= 0 && avgCalories.compareTo(BigDecimal.valueOf(2200)) <= 0) {
            score += 20;
        } else if (avgCalories.compareTo(BigDecimal.valueOf(1500)) >= 0 && avgCalories.compareTo(BigDecimal.valueOf(2500)) <= 0) {
            score += 10;
        }
        
        // 蛋白质充足性 (60-100g为最佳) +10分
        if (avgProtein.compareTo(BigDecimal.valueOf(60)) >= 0 && avgProtein.compareTo(BigDecimal.valueOf(100)) <= 0) {
            score += 10;
        } else if (avgProtein.compareTo(BigDecimal.valueOf(50)) >= 0) {
            score += 5;
        }
        
        // 营养素比例均衡 +10分
        BigDecimal total = avgProtein.add(avgCarb).add(avgFat);
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal proteinRatio = avgProtein.divide(total, 4, RoundingMode.HALF_UP);
            BigDecimal carbRatio = avgCarb.divide(total, 4, RoundingMode.HALF_UP);
            BigDecimal fatRatio = avgFat.divide(total, 4, RoundingMode.HALF_UP);
            
            // 理想比例: 蛋白质15-25%, 碳水50-65%, 脂肪20-30%
            if (proteinRatio.compareTo(BigDecimal.valueOf(0.15)) >= 0 && proteinRatio.compareTo(BigDecimal.valueOf(0.25)) <= 0
                && carbRatio.compareTo(BigDecimal.valueOf(0.50)) >= 0 && carbRatio.compareTo(BigDecimal.valueOf(0.65)) <= 0
                && fatRatio.compareTo(BigDecimal.valueOf(0.20)) >= 0 && fatRatio.compareTo(BigDecimal.valueOf(0.30)) <= 0) {
                score += 10;
            }
        }
        
        return Math.min(100, score);
    }

    @Override
    public NutritionReportVO getReportById(Long userId, Long id) {
        NutritionReport r = nutritionReportMapper.selectById(id);
        if (r == null) throw new BusinessException("报告不存在");
        if (!r.getUserId().equals(userId)) throw new BusinessException("无权限查看此报告");
        return toVO(r);
    }

    @Override
    public List<NutritionReportVO> getReportList(Long userId, ReportQueryDTO query) {
        List<NutritionReport> list = nutritionReportMapper.selectList(userId, query);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReport(Long userId, Long id) {
        NutritionReport r = nutritionReportMapper.selectById(id);
        if (r == null) throw new BusinessException("报告不存在");
        if (!r.getUserId().equals(userId)) throw new BusinessException("无权限删除此报告");
        nutritionReportMapper.deleteById(id);
    }

    @Override
    public NutritionReportVO getLatestReport(Long userId, String reportType) {
        NutritionReport r = nutritionReportMapper.selectByUserDateType(userId, LocalDate.now(), reportType);
        if (r == null) return null;
        return toVO(r);
    }

    @Override
    public NutritionTrendVO getNutritionTrend(Long userId, LocalDate startDate, LocalDate endDate) {
        List<NutritionReport> list = nutritionReportMapper.selectByDateRange(userId, startDate, endDate);
        if (list == null || list.isEmpty()) return null;

        BigDecimal sumCalories = BigDecimal.ZERO;
        BigDecimal sumProtein = BigDecimal.ZERO;
        BigDecimal sumCarb = BigDecimal.ZERO;
        BigDecimal sumFat = BigDecimal.ZERO;
        int count = 0;
        int sumScore = 0;
        for (NutritionReport r : list) {
            if (r.getTotalCalories() != null) sumCalories = sumCalories.add(r.getTotalCalories());
            if (r.getTotalProtein() != null) sumProtein = sumProtein.add(r.getTotalProtein());
            if (r.getTotalCarbohydrate() != null) sumCarb = sumCarb.add(r.getTotalCarbohydrate());
            if (r.getTotalFat() != null) sumFat = sumFat.add(r.getTotalFat());
            if (r.getNutritionBalanceScore() != null) sumScore += r.getNutritionBalanceScore();
            count++;
        }

        NutritionTrendVO vm = NutritionTrendVO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .avgCalories(sumCalories.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP))
                .avgProtein(sumProtein.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP))
                .avgCarbohydrate(sumCarb.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP))
                .avgFat(sumFat.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP))
                .avgScore(count > 0 ? BigDecimal.valueOf((double) sumScore / count).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .reportCount(count)
                .build();
        return vm;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void regenerateAIAdvice(Long userId, Long reportId) {
        NutritionReport r = nutritionReportMapper.selectById(reportId);
        if (r == null) throw new BusinessException("报告不存在");
        if (!r.getUserId().equals(userId)) throw new BusinessException("无权限操作此报告");

        String advice = nutritionAIService.generateAIAdvice(r);
        r.setAiAdvice(advice);
        nutritionReportMapper.updateById(r);
    }

    private NutritionReportVO toVO(NutritionReport r) {
        NutritionReportVO vo = new NutritionReportVO();
        vo.setId(r.getId());
        vo.setUserId(r.getUserId());
        vo.setReportDate(r.getReportDate());
        vo.setReportType(r.getReportType());
        vo.setReportPeriod(r.getReportType()); // 兼容前端
        vo.setReportTypeDesc(switch (r.getReportType()) {
            case "daily", "DAILY" -> "每日";
            case "weekly", "WEEK" -> "每周";
            case "monthly", "MONTH" -> "每月";
            case "QUARTER" -> "季度";
            default -> r.getReportType();
        });
        
        // 填充兼容前端的字段
        vo.setAvgCalories(r.getTotalCalories()); // 平均热量
        vo.setAvgProtein(r.getTotalProtein());
        vo.setAvgCarbohydrate(r.getTotalCarbohydrate());
        vo.setAvgFat(r.getTotalFat());
        vo.setCompliantDays(r.getGoalCompletionRate() != null ? r.getGoalCompletionRate().intValue() : 0); // 简化为完成率
        vo.setOverallScore(r.getNutritionBalanceScore() != null ? r.getNutritionBalanceScore() : 60); // 综合评分
        
        vo.setTotalCalories(r.getTotalCalories());
        vo.setTotalProtein(r.getTotalProtein());
        vo.setTotalCarbohydrate(r.getTotalCarbohydrate());
        vo.setTotalFat(r.getTotalFat());
        vo.setCaloriesBurned(r.getCaloriesBurned());
        vo.setNetCalories(r.getNetCalories());
        vo.setGoalCompletionRate(r.getGoalCompletionRate());
        vo.setNutritionBalanceScore(r.getNutritionBalanceScore());
        vo.setAiAdvice(r.getAiAdvice());
        vo.setCreatedAt(r.getCreatedAt());

        // 尝试设置报告期起止日期与总天数（便于前端请求趋势数据）
        try {
            LocalDate endDateForVO = r.getReportDate();
            LocalDate startDateForVO = endDateForVO;
            String typeForVO = r.getReportType() == null ? "" : r.getReportType().toUpperCase();
            switch (typeForVO) {
                case "WEEK":
                case "WEEKLY":
                    startDateForVO = endDateForVO.minusDays(6);
                    break;
                case "MONTH":
                case "MONTHLY":
                    startDateForVO = endDateForVO.withDayOfMonth(1);
                    break;
                case "QUARTER":
                    startDateForVO = endDateForVO.minusMonths(2).withDayOfMonth(1);
                    break;
                default:
                    break;
            }
            vo.setStartDate(startDateForVO);
            vo.setEndDate(endDateForVO);
            int totalDaysVO = (int) java.time.temporal.ChronoUnit.DAYS.between(startDateForVO, endDateForVO) + 1;
            vo.setTotalDays(totalDaysVO);
        } catch (Exception ex) {
            // ignore
        }

        // 简单计算 nutritionDetail（如果有值）
        NutritionReportVO.NutritionDetail nd = new NutritionReportVO.NutritionDetail();
        BigDecimal total = (r.getTotalProtein() == null ? BigDecimal.ZERO : r.getTotalProtein())
                .add(r.getTotalCarbohydrate() == null ? BigDecimal.ZERO : r.getTotalCarbohydrate())
                .add(r.getTotalFat() == null ? BigDecimal.ZERO : r.getTotalFat());
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            nd.setProteinRatio(r.getTotalProtein().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
            nd.setCarbRatio(r.getTotalCarbohydrate().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
            nd.setFatRatio(r.getTotalFat().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
        } else {
            nd.setProteinRatio(BigDecimal.ZERO);
            nd.setCarbRatio(BigDecimal.ZERO);
            nd.setFatRatio(BigDecimal.ZERO);
        }
        vo.setNutritionDetail(nd);

        // 简单营养等级
        vo.setNutritionGrade(vo.getNutritionBalanceScore() == null ? "C" : (vo.getNutritionBalanceScore() >= 80 ? "A" : vo.getNutritionBalanceScore() >= 60 ? "B" : "C"));

        // 设置目标营养值（标准推荐值）
        vo.setTargetCalories(BigDecimal.valueOf(2000)); // 标准成人每日推荐热量
        vo.setTargetProtein(BigDecimal.valueOf(60)); // 标准成人每日推荐蛋白质
        vo.setTargetFat(BigDecimal.valueOf(65)); // 标准成人每日推荐脂肪
        vo.setTargetCarbohydrate(BigDecimal.valueOf(275)); // 标准成人每日推荐碳水

        // 补充体重相关信息：尝试基于报告类型推断起止日期，并查询体重趋势
        try {
            LocalDate endDate = r.getReportDate();
            LocalDate startDate = endDate;
            String type = r.getReportType() == null ? "" : r.getReportType().toUpperCase();
            switch (type) {
                case "WEEK":
                case "WEEKLY":
                    startDate = endDate.minusDays(6);
                    break;
                case "MONTH":
                case "MONTHLY":
                    startDate = endDate.withDayOfMonth(1);
                    break;
                case "QUARTER":
                    startDate = endDate.minusMonths(2).withDayOfMonth(1);
                    break;
                default:
                    break;
            }

            com.health.agent.module.weight.vo.WeightTrendVO trend = weightRecordService.getWeightTrend(r.getUserId(), startDate, endDate);
            if (trend != null) {
                vo.setStartWeight(trend.getStartWeight());
                vo.setEndWeight(trend.getCurrentWeight());
                if (trend.getStartWeight() != null && trend.getCurrentWeight() != null) {
                    vo.setWeightChange(trend.getCurrentWeight().subtract(trend.getStartWeight()));
                    vo.setWeightChangeRate(trend.getChangeRate());
                }
            }
        } catch (Exception ex) {
            // 不影响主流程，记录日志即可
            log.debug("无法获取体重趋势: {}", ex.getMessage());
        }
        return vo;
    }
}
