<template>
  <div class="weight-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="体重管理" left-arrow @click-left="$router.back()" fixed />

    <!-- 最新体重卡片 -->
    <div class="latest-weight-card" v-if="latestRecord">
      <div class="weight-display">
        <div class="weight-value">{{ latestRecord.weight }}</div>
        <div class="weight-unit">kg</div>
      </div>
      <div class="weight-info">
        <div class="info-item">
          <span class="label">BMI</span>
          <span class="value">{{ latestRecord.bmi || '--' }}</span>
        </div>
        <div class="info-item">
          <span class="label">记录日期</span>
          <span class="value">{{ formatDate(latestRecord.recordDate) }}</span>
        </div>
      </div>
    </div>

    <!-- 快速添加按钮 -->
    <van-button
      round
      type="primary"
      block
      class="add-button"
      @click="showAddDialog = true"
    >
      <van-icon name="plus" /> 记录体重
    </van-button>



    <!-- 体重趋势 -->
    <div class="trend-container">
      <div class="section-title">体重趋势</div>
      <div class="date-range-selector">
        <van-button
          size="small"
          :type="dateRange === 7 ? 'primary' : 'default'"
          @click="changeDateRange(7)"
        >
          近7天
        </van-button>
        <van-button
          size="small"
          :type="dateRange === 30 ? 'primary' : 'default'"
          @click="changeDateRange(30)"
        >
          近30天
        </van-button>
        <van-button
          size="small"
          :type="dateRange === 90 ? 'primary' : 'default'"
          @click="changeDateRange(90)"
        >
          近90天
        </van-button>
      </div>

      <van-loading v-if="trendLoading" size="24px" vertical>加载中...</van-loading>
      
      <div v-else-if="trendData && trendData.dataPoints && trendData.dataPoints.length > 0" class="trend-content">
        <!-- 趋势统计 -->
        <div class="trend-stats">
          <div class="stat-item">
            <div class="stat-label">起始体重</div>
            <div class="stat-value">{{ trendData.startWeight }} kg</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">当前体重</div>
            <div class="stat-value primary">{{ trendData.currentWeight }} kg</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">体重变化</div>
            <div class="stat-value" :class="getTrendClass(trendData.totalChange)">
              {{ formatChange(trendData.totalChange) }} kg
            </div>
          </div>
        </div>

        <!-- 简单折线图 -->
        <div class="simple-chart">
          <div 
            v-for="(point, index) in trendData.dataPoints" 
            :key="index" 
            class="chart-point"
          >
            <div 
              class="point-bar" 
              :style="{ height: getBarHeight(point.weight) }"
              :title="`${formatShortDate(point.date)}: ${point.weight}kg`"
            >
              <span class="point-dot"></span>
            </div>
            <div class="point-label">{{ formatChartDate(point.date, index) }}</div>
          </div>
        </div>

        <!-- 数据摘要 -->
        <div class="trend-summary">
          <div class="summary-item">
            <span class="summary-label">记录次数：</span>
            <span class="summary-value">{{ trendData.recordCount }}次</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">平均体重：</span>
            <span class="summary-value">{{ trendData.avgWeight }} kg</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">最高体重：</span>
            <span class="summary-value">{{ trendData.maxWeight }} kg</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">最低体重：</span>
            <span class="summary-value">{{ trendData.minWeight }} kg</span>
          </div>
        </div>
      </div>

      <div v-else class="empty-trend">
        <van-empty description="暂无趋势数据，请先记录体重" />
      </div>
    </div>

    <!-- 添加体重弹窗 -->
    <van-dialog
      v-model:show="showAddDialog"
      title="记录体重"
      show-cancel-button
      @confirm="submitWeight"
    >
      <div class="add-form">
        <van-field v-model="addForm.weight" label="体重(kg)" type="number" placeholder="请输入体重" required />
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import {
  getLatestWeightRecord,
  addWeightRecord,
  getWeightTrend
} from '@/api/weight'

const router = useRouter()

const latestRecord = ref(null)
const trendData = ref(null)
const dateRange = ref(7)
const trendLoading = ref(false)
const showAddDialog = ref(false)
const addForm = ref({ weight: '' })

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '--'
  // 处理 LocalDate 格式 (yyyy-MM-dd)
  if (typeof dateStr === 'string' && dateStr.length === 10) {
    return dateStr
  }
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 加载最新记录
async function loadLatest() {
  try {
    const data = await getLatestWeightRecord()
    if (data) {
      latestRecord.value = data
    }
  } catch (error) {
    console.error('加载最新记录失败:', error)
  }
}

// 加载趋势数据
async function loadTrend() {
  try {
    trendLoading.value = true
    const endDate = new Date()
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - dateRange.value)

    const data = await getWeightTrend(
      startDate.toISOString().split('T')[0],
      endDate.toISOString().split('T')[0]
    )
    if (data) {
      trendData.value = data
    }
  } catch (error) {
    console.error('加载趋势失败:', error)
    showToast('加载趋势数据失败')
  } finally {
    trendLoading.value = false
  }
}

// 切换日期范围
function changeDateRange(range) {
  dateRange.value = range
  loadTrend()
}

// 计算柱状图高度（返回像素值）
function getBarHeight(weight) {
  if (!trendData.value || !trendData.value.dataPoints || trendData.value.dataPoints.length === 0) {
    return '60px'
  }
  const min = Number(trendData.value.minWeight)
  const max = Number(trendData.value.maxWeight)
  const range = max - min
  if (range === 0) return '60px'
  
  // 计算高度，范围在 20px 到 140px 之间
  const heightPx = 20 + ((Number(weight) - min) / range) * 120
  return Math.round(heightPx) + 'px'
}

// 格式化日期显示（图表用）
function formatChartDate(dateStr, index) {
  if (!dateStr) return ''
  // 处理 LocalDate 格式 (yyyy-MM-dd)
  if (typeof dateStr === 'string' && dateStr.length === 10) {
    const parts = dateStr.split('-')
    const month = parseInt(parts[1])
    const day = parseInt(parts[2])
    
    // 根据日期范围决定显示格式
    if (dateRange.value === 7) {
      // 7天显示所有日期
      return `${month}/${day}`
    } else if (dateRange.value === 30) {
      // 30天每隔几天显示一次
      return index % 3 === 0 ? `${month}/${day}` : ''
    } else {
      // 90天更稀疏显示
      return index % 7 === 0 ? `${month}/${day}` : ''
    }
  }
  return dateStr
}

// 格式化短日期
function formatShortDate(dateStr) {
  if (!dateStr) return '--'
  if (typeof dateStr === 'string' && dateStr.length === 10) {
    const parts = dateStr.split('-')
    return `${parseInt(parts[1])}月${parseInt(parts[2])}日`
  }
  return dateStr
}

// 格式化变化量
function formatChange(change) {
  if (!change) return '0.0'
  const num = Number(change)
  return num > 0 ? `+${num}` : num.toString()
}

// 获取趋势样式类
function getTrendClass(change) {
  if (!change) return ''
  const num = Number(change)
  return num > 0 ? 'increase' : num < 0 ? 'decrease' : ''
}

// 提交体重记录
async function submitWeight() {
  if (!addForm.value.weight) {
    showToast('请输入体重')
    return
  }

  try {
    // 格式化日期为 yyyy-MM-dd 格式（LocalDate）
    const today = new Date()
    const recordDate = today.toISOString().split('T')[0]
    
    await addWeightRecord({
      weight: Number(addForm.value.weight),
      recordDate: recordDate
    })
    showToast('记录成功')
    showAddDialog.value = false
    addForm.value = { weight: '' }
    
    // 重新加载最新数据和趋势
    await loadLatest()
    await loadTrend()
    
    // 广播体重更新事件，通知其它页面刷新
    try {
      window.dispatchEvent(new Event('weight-updated'))
    } catch (e) {
      // 在某些环境 window 可能不可用，忽略错误
    }
  } catch (error) {
    console.error('添加体重记录失败:', error)
    showToast(error.message || '记录失败')
  }
}



onMounted(() => {
  loadLatest()
  loadTrend()
})
</script>

<style scoped>
.weight-page {
  padding-top: 46px;
  padding-bottom: 20px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.latest-weight-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 12px;
  padding: 12px;
  border-radius: 8px;
  color: white;
  box-shadow: 0 1px 6px rgba(102, 126, 234, 0.2);
}

.weight-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 10px;
}

.weight-value {
  font-size: 30px;
  font-weight: bold;
}

.weight-unit {
  font-size: 20px;
  margin-left: 8px;
  opacity: 0.9;
}

.weight-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.info-item {
  text-align: center;
}

.info-item .label {
  display: block;
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.info-item .value {
  display: block;
  font-size: 15px;
  font-weight: 600;
}

.add-button {
  margin: 0 16px 12px;
}

/* 趋势容器 */
.trend-container {
  background: white;
  margin: 0 16px 16px;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.date-range-selector {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.trend-content {
  margin-top: 12px;
}

/* 趋势统计 */
.trend-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.stat-value.primary {
  color: #667eea;
}

.stat-value.increase {
  color: #ee0a24;
}

.stat-value.decrease {
  color: #07c160;
}

/* 简单图表 */
.simple-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 180px;
  padding: 16px 8px 40px;
  background: #fafbfc;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow-x: auto;
  position: relative;
}

.chart-point {
  flex: 1;
  min-width: 30px;
  max-width: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  height: 100%;
  position: relative;
}

.point-bar {
  width: 100%;
  max-width: 24px;
  background: linear-gradient(to top, #667eea, #9ca8ff);
  border-radius: 4px 4px 0 0;
  position: absolute;
  bottom: 24px;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.2);
  min-height: 4px;
}

.point-bar:hover {
  background: linear-gradient(to top, #5a6fd9, #8897ff);
  transform: translateY(-2px);
}

.point-dot {
  position: absolute;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 8px;
  height: 8px;
  background: #667eea;
  border: 2px solid white;
  border-radius: 50%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.point-label {
  font-size: 10px;
  color: #999;
  white-space: nowrap;
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) rotate(-45deg);
  transform-origin: center;
}

/* 趋势摘要 */
.trend-summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
}

.summary-item {
  font-size: 13px;
}

.summary-label {
  color: #666;
}

.summary-value {
  color: #333;
  font-weight: 500;
}

.empty-trend {
  padding: 40px 20px;
}

.add-form {
  padding: 16px;
}
</style>
