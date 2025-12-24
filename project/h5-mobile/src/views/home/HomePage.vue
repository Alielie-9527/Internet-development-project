<template>
  <div class="home-page">
    <!-- 头部 -->
    <div class="header">
      <h1 class="title">智能营养助手</h1>
      <p class="subtitle">您的健康饮食管家</p>
    </div>

    <!-- 今日数据概览 -->
    <div class="today-summary" v-if="userStore.isLoggedIn">
      <div class="summary-card">
        <div class="label">今日摄入</div>
        <div class="value">{{ todayCalories }} <span class="unit">千卡</span></div>
      </div>
      <div class="summary-card clickable" @click="goToWeight">
        <div class="label">当前体重</div>
        <div class="value">{{ currentWeight }} <span class="unit">kg</span></div>
        <div class="hint">点击记录体重</div>
      </div>
      <div class="summary-card clickable" @click="goToGoalSettings">
        <div class="label">目标达成</div>
        <div class="value" v-if="hasGoal">{{ goalProgressText }}<span class="unit">{{ goalUnit }}</span></div>
        <div class="no-goal" v-else>
          <van-icon name="setting-o" size="14" />
          <span>请设置目标</span>
        </div>
      </div>
    </div>

    <!-- 快捷功能 -->
    <div class="quick-actions">
      <div class="section-title">快捷功能</div>
      <div class="feature-cards">
        <div class="card" @click="goToChat">
          <van-icon name="chat-o" size="24" color="#667eea" />
          <h3>AI营养顾问</h3>
          <p>智能饮食建议</p>
        </div>

        <div class="card" @click="goToGoalSettings">
          <van-icon name="balance-list-o" size="24" color="#52c41a" />
          <h3>目标设置</h3>
          <p>设置健康目标</p>
        </div>

        <div class="card" @click="goToKnowledge">
          <van-icon name="bulb-o" size="24" color="#4a90e2" />
          <h3>营养知识</h3>
          <p>健康知识库</p>
        </div>

        <div class="card" @click="goToAssessment">
          <van-icon name="award-o" size="24" color="#faad14" />
          <h3>健康评估</h3>
          <p>测试健康状况</p>
        </div>
      </div>
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="active" fixed>
      <van-tabbar-item icon="wap-home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="records" to="/diet/list">饮食</van-tabbar-item>
      <van-tabbar-item icon="photograph" to="/food/analysis">拍照</van-tabbar-item>
      <van-tabbar-item icon="bar-chart-o" to="/report/list">报告</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/user">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getDietStatistics } from '@/api/diet'
import { getLatestWeightRecord } from '@/api/weight'

const router = useRouter()
const userStore = useUserStore()
const active = ref(0)

// 今日数据
const todayCalories = ref(0)
const currentWeight = ref('--')
const goalProgressText = ref('--')
const goalUnit = ref('%')
const hasGoal = ref(false)

// 加载今日数据
async function loadTodayData() {
  if (!userStore.isLoggedIn) return

  try {
    // 获取今日饮食统计
    const today = new Date().toISOString().split('T')[0]
    console.log('加载今日饮食统计，日期:', today)
    const dietData = await getDietStatistics(today)
    console.log('今日饮食统计数据:', dietData)
    if (dietData) {
      // 处理BigDecimal或字符串类型
      const calories = typeof dietData.totalCalories === 'number' 
        ? dietData.totalCalories 
        : parseFloat(dietData.totalCalories || 0)
      todayCalories.value = Math.round(calories)
    } else {
      todayCalories.value = 0
    }

    // 获取最新体重
    const weightData = await getLatestWeightRecord()
    console.log('最新体重数据:', weightData)
    if (weightData && weightData.weight) {
      currentWeight.value = weightData.weight
    } else {
      currentWeight.value = '--'
    }
    
    // 计算目标达成情况
    calculateGoalProgress()
  } catch (error) {
    console.error('加载今日数据失败:', error)
    // 失败时不显示错误，使用默认值
  }
}

// 计算目标达成进度
function calculateGoalProgress() {
  const goals = localStorage.getItem('userGoals')
  
  if (!goals) {
    hasGoal.value = false
    return
  }
  
  try {
    const goalsData = JSON.parse(goals)
    const targetWeight = parseFloat(goalsData.targetWeight)
    const current = parseFloat(currentWeight.value)
    const initialWeight = parseFloat(goalsData.initialWeight) // 使用initialWeight
    
    // 检查是否设置了目标体重和初始体重
    if (!targetWeight || !current || !initialWeight || isNaN(current)) {
      hasGoal.value = false
      return
    }
    
    hasGoal.value = true
    
    // 计算目标完成进度
    const totalChange = targetWeight - initialWeight // 目标变化量
    const currentChange = current - initialWeight // 当前变化量
    
    if (Math.abs(totalChange) < 0.1) {
      // 目标体重和初始体重相同（维持体重）
      goalProgressText.value = '维持中'
      goalUnit.value = ''
    } else {
      // 计算完成百分比
      // 更鲁棒的计算：按“已完成的绝对变化 / 总变化绝对值”计算进度，保证正向比例
      let progressRatio = 0
      if (totalChange > 0) {
        // 增重目标：已完成 = current - initial
        progressRatio = (current - initialWeight) / totalChange
      } else {
        // 减重目标：已完成 = initial - current
        progressRatio = (initialWeight - current) / Math.abs(totalChange)
      }
      const progressPercent = Math.round(progressRatio * 100)
      const clampedProgress = Math.max(0, Math.min(100, progressPercent))
      goalProgressText.value = clampedProgress.toString()
      goalUnit.value = '%'
    }
  } catch (e) {
    console.error('解析目标数据失败:', e)
    hasGoal.value = false
  }
}

function goToDiet() {
  router.push('/diet/list')
}

function goToFoodAnalysis() {
  router.push('/food/analysis')
}

function goToWeight() {
  router.push('/weight/list')
}

function goToReport() {
  router.push('/report/list')
}

function goToChat() {
  router.push('/chat')
}

function goToKnowledge() {
  router.push('/knowledge')
}

function goToGoalSettings() {
  router.push('/user/goal-settings')
}

function goToAssessment() {
  router.push('/assessment/list')
}

// 监听全局体重更新事件，保持首页数据同步
function onWeightUpdated() {
  loadTodayData()
}

// 监听页面可见性变化，页面激活时刷新数据
function handleVisibilityChange() {
  if (!document.hidden && userStore.isLoggedIn) {
    console.log('页面激活，刷新今日数据')
    loadTodayData()
  }
}

onMounted(() => {
  loadTodayData()
  window.addEventListener('weight-updated', onWeightUpdated)
  document.addEventListener('visibilitychange', handleVisibilityChange)
  
  // 监听自定义事件：饮食记录更新
  window.addEventListener('diet-updated', loadTodayData)
})

onBeforeUnmount(() => {
  window.removeEventListener('weight-updated', onWeightUpdated)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('diet-updated', loadTodayData)
})
</script>

<style scoped>
.home-page {
  padding-bottom: 60px;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 12px;
  text-align: center;
  color: white;
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.subtitle {
  font-size: 11px;
  opacity: 0.9;
}

.today-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
  padding: 10px;
  margin-top: -12px;
}

.summary-card {
  background: white;
  border-radius: 6px;
  padding: 8px 6px;
  text-align: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s;
}

.summary-card:active {
  transform: scale(0.98);
}

.summary-card .label {
  font-size: 10px;
  color: #999;
  margin-bottom: 4px;
}

.summary-card .value {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.summary-card .unit {
  font-size: 9px;
  font-weight: normal;
  color: #666;
}

.summary-card .no-goal {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 10px;
  color: #faad14;
  padding: 4px 0;
}

.summary-card .no-goal span {
  font-size: 10px;
}

.summary-card .hint {
  font-size: 9px;
  color: #999;
  margin-top: 2px;
}

.summary-card.clickable {
  cursor: pointer;
}

.summary-card.clickable:hover {
  background: #f7f8fa;
}

.quick-actions {
  padding: 10px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  padding: 0 2px;
}

.feature-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.card {
  background: white;
  border-radius: 6px;
  padding: 10px 8px;
  text-align: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: transform 0.2s;
  min-height: 75px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.card:active {
  transform: scale(0.98);
}

.card h3 {
  font-size: 12px;
  margin: 6px 0 3px;
  color: #333;
  font-weight: 600;
}

.card p {
  font-size: 10px;
  color: #999;
  line-height: 1.2;
}
</style>

