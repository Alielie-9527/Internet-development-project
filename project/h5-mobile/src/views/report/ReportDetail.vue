<template>
  <div class="report-detail-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="营养报告详情" left-arrow @click-left="$router.back()" fixed>
      <template #right>
        <van-icon name="share-o" size="18" @click="shareReport" />
      </template>
    </van-nav-bar>

    <div class="content" v-if="report">
      <!-- 报告头部 -->
      <div class="report-header-card">
        <div class="header-period">
          <van-tag :type="getPeriodTagType(report.reportPeriod)" size="large">
            {{ getPeriodLabel(report.reportPeriod) }}
          </van-tag>
          <div class="header-date">{{ report.reportDate }}</div>
        </div>
        <div class="header-score">
          <div class="score-value" :class="getScoreClass(report.overallScore)">
            {{ report.overallScore }}
          </div>
          <div class="score-label">综合评分</div>
        </div>
      </div>

      <!-- 营养摄入统计 -->
      <div class="section-card">
        <div class="section-title">
          <van-icon name="chart-trending-o" />
          <span>营养摄入统计</span>
        </div>
        <div class="nutrition-stats">
          <div class="stat-item">
            <div class="stat-label">平均热量</div>
            <div class="stat-value">{{ report.avgCalories }}<span class="unit">千卡</span></div>
            <div class="stat-target">目标: {{ report.targetCalories }}千卡</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">蛋白质</div>
            <div class="stat-value">{{ report.avgProtein }}<span class="unit">g</span></div>
            <div class="stat-target">目标: {{ report.targetProtein }}g</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">脂肪</div>
            <div class="stat-value">{{ report.avgFat }}<span class="unit">g</span></div>
            <div class="stat-target">目标: {{ report.targetFat }}g</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">碳水化合物</div>
            <div class="stat-value">{{ report.avgCarbohydrate }}<span class="unit">g</span></div>
            <div class="stat-target">目标: {{ report.targetCarbohydrate }}g</div>
          </div>
        </div>
      </div>

      <!-- 达标情况 -->
      <div class="section-card">
        <div class="section-title">
          <van-icon name="medal-o" />
          <span>达标情况</span>
        </div>
        <div class="compliance-grid">
          <div class="compliance-item">
            <div class="compliance-value">{{ report.compliantDays }}</div>
            <div class="compliance-label">热量达标天数</div>
          </div>
          <div class="compliance-item">
            <div class="compliance-value">{{ report.totalDays }}</div>
            <div class="compliance-label">总天数</div>
          </div>
          <div class="compliance-item">
            <div class="compliance-value">{{ calculateComplianceRate() }}%</div>
            <div class="compliance-label">热量达标率</div>
          </div>
          <div class="compliance-item">
            <div class="compliance-value">{{ calculateWeightGoalProgress() }}%</div>
            <div class="compliance-label">体重目标完成度</div>
          </div>
        </div>
      </div>

      <!-- 体重变化 -->
      <div class="section-card">
        <div class="section-title">
          <van-icon name="trend-pie" />
          <span>体重变化</span>
        </div>
        <div class="weight-change">
          <div class="weight-item">
            <div class="label">期初体重</div>
            <div class="value">{{ report.startWeight || '--' }} kg</div>
          </div>
          <div class="weight-item">
            <div class="label">期末体重</div>
            <div class="value">{{ report.endWeight || '--' }} kg</div>
          </div>
          <div class="weight-item">
            <div class="label">变化</div>
            <div class="value">{{ report.weightChange ? report.weightChange.toFixed(2) : '--' }} kg ({{ report.weightChangeRate ? report.weightChangeRate + '%' : '--' }})</div>
          </div>
        </div>
      </div>

      <!-- AI营养建议 -->
      <div class="section-card ai-advice-card">
        <div class="section-title">
          <van-icon name="bulb-o" color="#faad14" />
          <span>AI营养建议</span>
          <van-button
            type="primary"
            size="mini"
            round
            plain
            :loading="regenerating"
            @click="regenerateAdvice"
          >
            重新生成
          </van-button>
        </div>
        <div class="ai-advice-content">
          {{ report.aiAdvice || '暂无营养建议' }}
        </div>
      </div>

      <!-- 趋势图表 -->
      <div class="section-card">
        <div class="section-title">
          <van-icon name="analytics-o" />
          <span>营养趋势</span>
        </div>
        <div class="trend-chart">
          <!-- 简单的柱状图展示 -->
          <div class="chart-container">
            <div v-for="day in trendData" :key="day.date" class="chart-bar-wrapper">
              <div class="chart-bar">
                <div
                  class="bar-fill"
                  :style="{
                    height: `${(day.calories / report.targetCalories) * 100}%`,
                    backgroundColor: day.calories > report.targetCalories ? '#ff4d4f' : '#52c41a'
                  }"
                ></div>
              </div>
              <div class="chart-label">{{ formatDay(day.date) }}</div>
            </div>
          </div>
          <div class="chart-legend">
            <span class="legend-item">
              <span class="legend-color" style="background: #52c41a;"></span>
              达标
            </span>
            <span class="legend-item">
              <span class="legend-color" style="background: #ff4d4f;"></span>
              超标
            </span>
          </div>
        </div>
      </div>

      <!-- 生成时间 -->
      <div class="report-footer">
        <van-icon name="clock-o" />
        <span>生成时间: {{ formatDateTime(report.createdAt) }}</span>
      </div>
    </div>

    <!-- 加载中 -->
    <van-loading v-else size="32" class="loading-container">
      加载中...
    </van-loading>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getReportDetail, regenerateAIAdvice, getNutritionTrend } from '@/api/nutrition'

const route = useRoute()
const router = useRouter()

const report = ref(null)
const trendData = ref([])
const regenerating = ref(false)

onMounted(() => {
  loadReportDetail()
})

// 加载报告详情
async function loadReportDetail() {
  try {
    // http拦截器已经提取了data字段,直接使用
    const data = await getReportDetail(route.params.id)
    report.value = data
    loadTrendData()
  } catch (error) {
    console.error('加载报告详情失败:', error)
    showDialog({
      title: '加载失败',
      message: error.message || '报告不存在'
    }).then(() => {
      router.back()
    })
  }
}

// 加载趋势数据
async function loadTrendData() {
  try {
    // http拦截器已经提取了data字段
    const data = await getNutritionTrend(report.value.startDate, report.value.endDate)
    trendData.value = data || []
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

// 重新生成AI建议
async function regenerateAdvice() {
  regenerating.value = true
  try {
    // regenerateAIAdvice API返回void, 需要重新加载报告
    await regenerateAIAdvice(route.params.id)
    showToast('建议已更新')
    // 重新加载报告数据
    await loadReportDetail()
  } catch (error) {
    console.error('重新生成AI建议失败:', error)
    showToast(error.message || '网络错误')
  } finally {
    regenerating.value = false
  }
}

// 分享报告
function shareReport() {
  showDialog({
    title: '分享报告',
    message: '分享功能开发中...'
  })
}

// 计算达标率
function calculateComplianceRate() {
  if (!report.value || report.value.totalDays === 0) return 0
  return Math.round((report.value.compliantDays / report.value.totalDays) * 100)
}

// 计算体重目标完成度
function calculateWeightGoalProgress() {
  if (!report.value) return 0
  
  // 从localStorage获取用户目标设置
  const goals = localStorage.getItem('userGoals')
  if (!goals) return 0
  
  try {
    const goalsData = JSON.parse(goals)
    const targetWeight = parseFloat(goalsData.targetWeight)
    const initialWeight = parseFloat(goalsData.initialWeight)
    const currentWeight = report.value.endWeight ? parseFloat(report.value.endWeight) : null
    
    // 检查必要数据是否完整
    if (!targetWeight || !initialWeight || !currentWeight || isNaN(currentWeight)) {
      return 0
    }
    
    const totalChange = targetWeight - initialWeight
    
    // 如果目标和初始体重相同（维持体重）
    if (Math.abs(totalChange) < 0.1) {
      return 100
    }
    
    // 计算完成度
    let progressRatio = 0
    if (totalChange > 0) {
      // 增重目标
      progressRatio = (currentWeight - initialWeight) / totalChange
    } else {
      // 减重目标
      progressRatio = (initialWeight - currentWeight) / Math.abs(totalChange)
    }
    
    const progressPercent = Math.round(progressRatio * 100)
    return Math.max(0, Math.min(100, progressPercent))
  } catch (e) {
    console.error('计算体重目标完成度失败:', e)
    return 0
  }
}

// 获取周期标签类型
function getPeriodTagType(period) {
  const typeMap = {
    'WEEK': 'primary',
    'MONTH': 'success',
    'QUARTER': 'warning'
  }
  return typeMap[period] || 'default'
}

// 获取周期标签
function getPeriodLabel(period) {
  const labelMap = {
    'WEEK': '周报告',
    'MONTH': '月报告',
    'QUARTER': '季度报告'
  }
  return labelMap[period] || period
}

// 获取评分样式类
function getScoreClass(score) {
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-medium'
  return 'score-poor'
}

// 格式化日期
function formatDay(dateStr) {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 格式化日期时间
function formatDateTime(datetime) {
  const date = new Date(datetime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.report-detail-page {
  padding-top: 46px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.content {
  padding: 16px;
  padding-bottom: 32px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.report-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-period {
  flex: 1;
}

.header-date {
  font-size: 15px;
  margin-top: 6px;
  opacity: 0.9;
}

.header-score {
  text-align: right;
}

.score-value {
  font-size: 42px;
  font-weight: bold;
  line-height: 1;
}

.score-value.score-good {
  color: #52c41a;
}

.score-value.score-medium {
  color: #faad14;
}

.score-value.score-poor {
  color: #ff4d4f;
}

.score-label {
  font-size: 13px;
  margin-top: 6px;
  opacity: 0.9;
}

.section-card {
  background: white;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 12px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.section-title .van-button {
  margin-left: auto;
}

.nutrition-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background-color: #f7f8fa;
  border-radius: 8px;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-value .unit {
  font-size: 11px;
  font-weight: normal;
  color: #999;
  margin-left: 2px;
}

.stat-target {
  font-size: 11px;
  color: #666;
  margin-top: 4px;
}

.compliance-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.compliance-item {
  text-align: center;
}

.compliance-value {
  font-size: 28px;
  font-weight: bold;
  color: #1989fa;
}

.compliance-label {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}

.ai-advice-card {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.ai-advice-content {
  font-size: 13px;
  line-height: 1.7;
  color: #333;
}

.trend-chart {
  margin-top: 16px;
}

.chart-container {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 180px;
  padding: 0 8px;
  border-bottom: 2px solid #e0e0e0;
}

.chart-bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 2px;
}

.chart-bar {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bar-fill {
  width: 80%;
  border-radius: 4px 4px 0 0;
  transition: height 0.3s;
  min-height: 4px;
}

.chart-label {
  font-size: 10px;
  color: #666;
  margin-top: 6px;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.report-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
  margin-top: 24px;
}
</style>
