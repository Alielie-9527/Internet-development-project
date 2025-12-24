<template>
  <div class="diet-detail-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="饮食详情" left-arrow @click-left="$router.back()" fixed>
      <template #right>
        <van-icon name="edit" size="20" @click="goToEdit" />
      </template>
    </van-nav-bar>

    <div v-if="loading" class="loading-container">
      <van-loading type="spinner">加载中...</van-loading>
    </div>

    <div v-else-if="detail" class="detail-container">
      <!-- 餐次信息 -->
      <div class="section meal-info">
        <div class="meal-type">
          <van-icon :name="getMealIcon(detail.mealType)" />
          <span>{{ getMealTypeName(detail.mealType) }}</span>
        </div>
        <div class="meal-time">{{ formatDateTime(detail.mealTime) }}</div>
      </div>

      <!-- 食物信息 -->
      <div class="section food-info">
        <div class="food-name">{{ detail.foodName }}</div>
        <div class="food-portion">
          <span class="label">食用分量：</span>
          <span class="value">{{ detail.portion }}{{ detail.unit }}</span>
        </div>
      </div>

      <!-- 营养信息 -->
      <div class="section nutrition-info">
        <div class="section-title">营养成分</div>
        <div class="nutrition-grid">
          <div class="nutrition-item">
            <div class="nutrition-label">热量</div>
            <div class="nutrition-value">{{ detail.calories }}</div>
            <div class="nutrition-unit">千卡</div>
          </div>
          <div class="nutrition-item">
            <div class="nutrition-label">蛋白质</div>
            <div class="nutrition-value">{{ detail.protein }}</div>
            <div class="nutrition-unit">g</div>
          </div>
          <div class="nutrition-item">
            <div class="nutrition-label">脂肪</div>
            <div class="nutrition-value">{{ detail.fat }}</div>
            <div class="nutrition-unit">g</div>
          </div>
          <div class="nutrition-item">
            <div class="nutrition-label">碳水</div>
            <div class="nutrition-value">{{ detail.carbohydrate }}</div>
            <div class="nutrition-unit">g</div>
          </div>
        </div>
      </div>

      <!-- 备注 -->
      <div v-if="detail.notes" class="section notes-info">
        <div class="section-title">备注</div>
        <div class="notes-content">{{ detail.notes }}</div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button round block type="primary" @click="goToEdit">
          编辑记录
        </van-button>
        <van-button round block type="danger" plain @click="confirmDelete">
          删除记录
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getDietDiaryDetail, deleteDietDiary } from '@/api/diet'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const detail = ref(null)

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

// 格式化日期时间
function formatDateTime(datetime) {
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 加载详情
async function loadDetail() {
  loading.value = true
  try {
    const data = await getDietDiaryDetail(route.params.id)
    console.log('饮食详情数据:', data)
    if (data) {
      detail.value = data
    } else {
      showToast('加载失败')
    }
  } catch (error) {
    showToast('加载失败')
    console.error('加载详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到编辑页面
function goToEdit() {
  router.push({ name: 'DietEdit', params: { id: route.params.id } })
}

// 确认删除
async function confirmDelete() {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这条饮食记录吗？',
      confirmButtonText: '删除',
      confirmButtonColor: '#ee0a24'
    })
    await handleDelete()
  } catch {
    // 用户取消删除
  }
}

// 删除记录
async function handleDelete() {
  try {
    await deleteDietDiary(route.params.id)
    showToast('删除成功')
    // 触发饮食记录更新事件
    window.dispatchEvent(new Event('diet-updated'))
    router.back()
  } catch (error) {
    showToast('删除失败')
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.diet-detail-page {
  padding-top: 46px;
  background-color: #f7f8fa;
  min-height: 100vh;
  padding-bottom: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.detail-container {
  padding: 12px;
}

.section {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.meal-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meal-type {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.meal-time {
  font-size: 14px;
  color: #999;
}

.food-info {
  text-align: center;
}

.food-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.food-portion {
  font-size: 14px;
  color: #666;
}

.food-portion .label {
  color: #999;
}

.food-portion .value {
  color: #4a90e2;
  font-weight: 500;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.nutrition-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.nutrition-item {
  text-align: center;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
}

.nutrition-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.nutrition-value {
  font-size: 20px;
  font-weight: 600;
  color: #4a90e2;
  margin-bottom: 4px;
}

.nutrition-unit {
  font-size: 12px;
  color: #666;
}

.notes-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.action-buttons {
  padding: 0 12px;
  margin-top: 24px;
}

.action-buttons .van-button {
  margin-bottom: 12px;
}
</style>
