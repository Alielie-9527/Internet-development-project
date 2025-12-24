<template>
  <div class="report-list-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="营养报告" left-arrow @click-left="$router.back()" fixed />

    <div class="content">
      <!-- 报告周期选择 -->
      <div class="period-selector">
        <van-button
          v-for="period in periods"
          :key="period.value"
          :type="selectedPeriod === period.value ? 'primary' : 'default'"
          size="small"
          round
          @click="selectPeriod(period.value)"
        >
          {{ period.label }}
        </van-button>
      </div>

      <!-- 生成新报告 -->
      <van-button
        type="primary"
        round
        block
        icon="add-o"
        class="generate-btn"
        :loading="generating"
        @click="generateNewReport"
      >
        生成{{ currentPeriodLabel }}报告
      </van-button>

      <!-- 报告列表 -->
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadReportList"
      >
        <div
          v-for="report in reportList"
          :key="report.id"
          class="report-card"
          @click="viewReport(report)"
        >
          <div class="report-header">
            <div class="report-period">
              <van-tag :type="getPeriodTagType(report.reportPeriod)">
                {{ getPeriodLabel(report.reportPeriod) }}
              </van-tag>
              <span class="report-date">{{ report.reportDate }}</span>
            </div>
            <van-icon name="arrow" />
          </div>

          <div class="report-summary">
            <div class="summary-item">
              <div class="summary-label">平均摄入</div>
              <div class="summary-value">{{ report.avgCalories }}<span class="unit">千卡/天</span></div>
            </div>
            <div class="summary-item">
              <div class="summary-label">达标天数</div>
              <div class="summary-value">{{ report.compliantDays }}<span class="unit">天</span></div>
            </div>
            <div class="summary-item">
              <div class="summary-label">综合评分</div>
              <div class="summary-value" :class="getScoreClass(report.overallScore)">
                {{ report.overallScore }}
              </div>
            </div>
          </div>

          <div class="report-advice" v-if="report.aiAdvice">
            <van-icon name="bulb-o" />
            <span>{{ truncateText(report.aiAdvice, 50) }}</span>
          </div>
        </div>
      </van-list>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && reportList.length === 0"
        description="还没有营养报告"
        image="search"
      >
        <van-button round type="primary" @click="generateNewReport">
          立即生成
        </van-button>
      </van-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getReportList, generateReport } from '@/api/nutrition'

const router = useRouter()

const periods = [
  { label: '周报告', value: 'WEEK' },
  { label: '月报告', value: 'MONTH' },
  { label: '季度报告', value: 'QUARTER' }
]

const selectedPeriod = ref('WEEK')
const reportList = ref([])
const loading = ref(false)
const finished = ref(false)
const generating = ref(false)
const page = ref(1)
const pageSize = 10

const currentPeriodLabel = computed(() => {
  const period = periods.find(p => p.value === selectedPeriod.value)
  return period ? period.label : '周报告'
})

// 选择周期
function selectPeriod(period) {
  if (selectedPeriod.value !== period) {
    selectedPeriod.value = period
    reportList.value = []
    page.value = 1
    finished.value = false
    loadReportList()
  }
}

// 加载报告列表
async function loadReportList() {
  if (loading.value) return

  loading.value = true
  try {
    const data = await getReportList({
      reportPeriod: selectedPeriod.value,
      pageNum: page.value,
      pageSize: pageSize
    })

    // 后端直接返回数组（http拦截器已经提取了data字段）
    const newReports = Array.isArray(data) ? data : []

    reportList.value = [...reportList.value, ...newReports]

    if (newReports.length < pageSize) {
      finished.value = true
    } else {
      page.value++
    }
  } catch (error) {
    console.error('加载报告列表失败:', error)
    showToast(error.message || '加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 生成新报告
async function generateNewReport() {
  generating.value = true
  try {
    // 计算日期范围
    const endDate = new Date()
    let startDate = new Date()
    
    switch(selectedPeriod.value) {
      case 'WEEK':
        startDate.setDate(endDate.getDate() - 6) // 包含今天共7天
        break
      case 'MONTH':
        startDate.setMonth(endDate.getMonth() - 1)
        startDate.setDate(endDate.getDate()) // 保持同一天
        break
      case 'QUARTER':
        startDate.setMonth(endDate.getMonth() - 3)
        startDate.setDate(endDate.getDate())
        break
    }

    const reportId = await generateReport({
      reportPeriod: selectedPeriod.value,
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      useAI: true
    })

    if (reportId) {
      showToast('报告生成成功')
      
      // 刷新列表
      reportList.value = []
      page.value = 1
      finished.value = false
      await loadReportList()
      
      // 跳转到详情
      setTimeout(() => {
        router.push({
          name: 'ReportDetail',
          params: { id: reportId }
        })
      }, 500)
    } else {
      throw new Error('报告生成失败，未返回报告ID')
    }
  } catch (error) {
    console.error('生成报告失败:', error)
    showDialog({
      title: '生成失败',
      message: error.message || '请确保已记录足够的饮食数据后再生成报告'
    })
  } finally {
    generating.value = false
  }
}

// 查看报告详情
function viewReport(report) {
  router.push({
    name: 'ReportDetail',
    params: { id: report.id }
  })
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

// 截断文本
function truncateText(text, maxLength) {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}
</script>

<style scoped>
.report-list-page {
  padding-top: 46px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.content {
  padding: 12px;
}

.period-selector {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.generate-btn {
  margin-bottom: 16px;
}

.report-card {
  background: white;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.report-card:active {
  transform: scale(0.98);
  background-color: #fafafa;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.report-period {
  display: flex;
  align-items: center;
  gap: 8px;
}

.report-date {
  font-size: 14px;
  color: #666;
}

.report-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.summary-value .unit {
  font-size: 12px;
  font-weight: normal;
  color: #999;
  margin-left: 2px;
}

.summary-value.score-good {
  color: #52c41a;
}

.summary-value.score-medium {
  color: #faad14;
}

.summary-value.score-poor {
  color: #ff4d4f;
}

.report-advice {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 12px;
  padding: 12px;
  background-color: #f0f9ff;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}
</style>
