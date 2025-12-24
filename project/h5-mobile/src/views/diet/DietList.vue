<template>
  <div class="diet-list-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="饮食日记" left-arrow @click-left="$router.back()" fixed>
      <template #right>
        <van-icon name="add-o" size="20" @click="goToAdd" />
      </template>
    </van-nav-bar>

    <!-- 日期选择 -->
    <div class="date-picker">
      <van-icon name="arrow-left" @click="prevDate" />
      <div class="date-display" @click="showDatePicker = true">
        {{ formatDate(selectedDate) }}
      </div>
      <van-icon name="arrow" @click="nextDate" />
    </div>

    <!-- 营养摘要 -->
    <div class="nutrition-summary" v-if="statistics">
      <div class="date-label">{{ getDateLabel() }}营养摄入</div>
      <div class="summary-items">
        <div class="summary-item">
          <div class="label">总热量</div>
          <div class="value">{{ statistics.totalCalories }}</div>
          <div class="unit">千卡</div>
        </div>
        <div class="summary-item">
          <div class="label">蛋白质</div>
          <div class="value">{{ statistics.totalProtein }}</div>
          <div class="unit">g</div>
        </div>
        <div class="summary-item">
          <div class="label">脂肪</div>
          <div class="value">{{ statistics.totalFat }}</div>
          <div class="unit">g</div>
        </div>
        <div class="summary-item">
          <div class="label">碳水</div>
          <div class="value">{{ statistics.totalCarbohydrate }}</div>
          <div class="unit">g</div>
        </div>
      </div>
    </div>

    <!-- 饮食记录列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div class="meal-groups">
        <div v-for="(meals, type) in groupedMeals" :key="type" class="meal-group">
          <div class="meal-type-header">
            <van-icon :name="getMealIcon(type)" />
            <span>{{ getMealTypeName(type) }}</span>
          </div>
          <div class="meal-items">
            <div
              v-for="meal in meals"
              :key="meal.id"
              class="meal-item"
              @click="goToDetail(meal.id)"
            >
              <div class="meal-info">
                <div class="food-name">{{ meal.foodName }}</div>
                <div class="meal-details">
                  <span>{{ meal.portion }}{{ meal.unit }}</span>
                  <span class="separator">·</span>
                  <span>{{ meal.calories }}千卡</span>
                  <span class="separator">·</span>
                  <span>{{ formatTime(meal.mealTime) }}</span>
                </div>
              </div>
              <van-icon name="arrow" class="arrow-icon" />
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty
          v-if="!loading && diaryList.length === 0"
          :description="`${getDateLabel()}还没有饮食记录`"
        >
          <van-button round type="primary" @click="goToAdd">添加记录</van-button>
        </van-empty>
      </div>
    </van-pull-refresh>

    <!-- 日期选择器 -->
    <van-calendar
      v-model:show="showDatePicker"
      :max-date="new Date()"
      @confirm="onDateConfirm"
    />

    <!-- 添加按钮 -->
    <van-button
      round
      type="primary"
      size="large"
      class="add-button"
      icon="plus"
      @click="goToAdd"
    >
      添加饮食记录
    </van-button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getDietDiaryByDate, getDietStatistics } from '@/api/diet'

const router = useRouter()

const selectedDate = ref(new Date())
const showDatePicker = ref(false)
const diaryList = ref([])
const statistics = ref(null)
const loading = ref(false)
const refreshing = ref(false)

// 按餐次分组
const groupedMeals = computed(() => {
  const groups = {}
  diaryList.value.forEach(meal => {
    if (!groups[meal.mealType]) {
      groups[meal.mealType] = []
    }
    groups[meal.mealType].push(meal)
  })
  return groups
})

// 获取餐次图标
function getMealIcon(type) {
  const icons = {
    'BREAKFAST': 'sun-o',
    'LUNCH': 'fire',
    'DINNER': 'moon-o',
    'SNACK': 'smile-o'
  }
  return icons[type] || 'star-o'
}

// 获取餐次名称
function getMealTypeName(type) {
  const names = {
    'BREAKFAST': '早餐',
    'LUNCH': '午餐',
    'DINNER': '晚餐',
    'SNACK': '加餐'
  }
  return names[type] || type
}

// 格式化日期
function formatDate(date) {
  const today = new Date()
  const target = new Date(date)
  
  if (target.toDateString() === today.toDateString()) {
    return '今天'
  }
  
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  if (target.toDateString() === yesterday.toDateString()) {
    return '昨天'
  }
  
  return `${target.getMonth() + 1}月${target.getDate()}日`
}

// 获取日期标签（用于提示文本）
function getDateLabel() {
  const today = new Date()
  const target = new Date(selectedDate.value)
  
  // 重置时间部分，只比较日期
  today.setHours(0, 0, 0, 0)
  target.setHours(0, 0, 0, 0)
  
  if (target.getTime() === today.getTime()) {
    return '今日'
  }
  
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  if (target.getTime() === yesterday.getTime()) {
    return '昨日'
  }
  
  return `${target.getMonth() + 1}月${target.getDate()}日`
}

// 格式化时间
function formatTime(datetime) {
  const date = new Date(datetime)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const dateStr = selectedDate.value.toISOString().split('T')[0]
    console.log('加载饮食数据，日期:', dateStr)
    
    // 获取饮食记录
    const diaryData = await getDietDiaryByDate(dateStr)
    console.log('饮食记录数据:', diaryData)
    diaryList.value = diaryData || []
    
    // 获取营养统计
    const statsData = await getDietStatistics(dateStr)
    console.log('营养统计数据:', statsData)
    
    // 处理统计数据（可能是BigDecimal类型）
    if (statsData) {
      statistics.value = {
        totalCalories: parseFloat(statsData.totalCalories || 0).toFixed(0),
        totalProtein: parseFloat(statsData.totalProtein || 0).toFixed(1),
        totalFat: parseFloat(statsData.totalFat || 0).toFixed(1),
        totalCarbohydrate: parseFloat(statsData.totalCarbohydrate || 0).toFixed(1)
      }
    } else {
      statistics.value = {
        totalCalories: '0',
        totalProtein: '0',
        totalFat: '0',
        totalCarbohydrate: '0'
      }
    }
  } catch (error) {
    showToast('加载失败')
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 刷新
function onRefresh() {
  loadData()
}

// 前一天
function prevDate() {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() - 1)
  selectedDate.value = date
  loadData()
}

// 后一天
function nextDate() {
  const date = new Date(selectedDate.value)
  const today = new Date()
  if (date < today) {
    date.setDate(date.getDate() + 1)
    selectedDate.value = date
    loadData()
  }
}

// 日期确认
function onDateConfirm(value) {
  selectedDate.value = value
  showDatePicker.value = false
  loadData()
}

// 跳转到添加页面
function goToAdd() {
  // 将选中的日期传递给添加页面
  const dateStr = selectedDate.value.toISOString().split('T')[0]
  console.log('跳转到添加页面，传递日期:', dateStr)
  router.push({
    name: 'DietAdd',
    query: { date: dateStr }
  })
}

// 跳转到详情页面
function goToDetail(id) {
  router.push({ name: 'DietDetail', params: { id } })
}

// 监听饮食记录更新事件
function onDietUpdated() {
  console.log('收到饮食记录更新事件，刷新数据')
  loadData()
}

// 监听页面可见性变化
function handleVisibilityChange() {
  if (!document.hidden) {
    console.log('页面激活，刷新饮食数据')
    loadData()
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('diet-updated', onDietUpdated)
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('diet-updated', onDietUpdated)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.diet-list-page {
  padding-top: 46px;
  padding-bottom: 80px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.date-picker {
  background: white;
  padding: 10px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.date-display {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  flex: 1;
  text-align: center;
}

.nutrition-summary {
  background: white;
  padding: 12px;
  margin-bottom: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.date-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.summary-items {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
}

.summary-item {
  text-align: center;
}

.summary-item .label {
  font-size: 10px;
  color: #999;
  margin-bottom: 3px;
}

.summary-item .value {
  font-size: 14px;
  font-weight: bold;
  color: #4a90e2;
}

.summary-item .unit {
  font-size: 9px;
  color: #666;
}

.meal-groups {
  padding: 8px 12px;
}

.meal-group {
  margin-bottom: 16px;
}

.meal-type-header {
  display: flex;
  align-items: center;
  padding: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #333;
  gap: 4px;
}

.meal-items {
  background: white;
  border-radius: 6px;
  overflow: hidden;
}

.meal-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.meal-item:last-child {
  border-bottom: none;
}

.meal-item:active {
  background-color: #f7f8fa;
}

.meal-info {
  flex: 1;
}

.food-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 3px;
}

.meal-details {
  font-size: 11px;
  color: #999;
}

.separator {
  margin: 0 6px;
}

.arrow-icon {
  color: #c8c9cc;
}

.add-button {
  position: fixed;
  bottom: 70px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 32px);
  max-width: 400px;
}
</style>
