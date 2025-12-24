<template>
  <div class="food-analysis-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="AI识别食物" left-arrow @click-left="$router.back()" fixed />

    <div class="content">
      <!-- 上传区域 -->
      <div class="upload-section">
        <van-uploader
          v-model="fileList"
          :max-count="1"
          :after-read="afterRead"
          :before-read="beforeRead"
        >
          <div class="upload-area">
            <van-icon name="photograph" size="36" color="#1989fa" />
            <div class="upload-text">点击拍照或选择图片</div>
            <div class="upload-hint">AI将自动识别食物营养成分</div>
          </div>
        </van-uploader>
      </div>

      <!-- 分析中 -->
      <van-loading v-if="analyzing" size="32" class="analyzing">
        <template #default>AI正在分析中...</template>
      </van-loading>

      <!-- 分析结果 -->
      <div v-if="result && !analyzing" class="result-section">
        <div class="result-header">
          <van-icon name="checked" color="#52c41a" size="20" />
          <span class="result-title">识别成功</span>
        </div>

        <!-- 食物信息 -->
        <div class="food-info-card">
          <div class="food-name">{{ result.food.name }}</div>
          <div class="food-category">{{ result.food.category }}</div>

          <div class="nutrition-grid">
            <div class="nutrition-item">
              <div class="nutrition-label">热量</div>
              <div class="nutrition-value">{{ result.food.calories }}</div>
              <div class="nutrition-unit">千卡</div>
            </div>
            <div class="nutrition-item">
              <div class="nutrition-label">蛋白质</div>
              <div class="nutrition-value">{{ result.food.protein }}</div>
              <div class="nutrition-unit">g</div>
            </div>
            <div class="nutrition-item">
              <div class="nutrition-label">脂肪</div>
              <div class="nutrition-value">{{ result.food.fat }}</div>
              <div class="nutrition-unit">g</div>
            </div>
            <div class="nutrition-item">
              <div class="nutrition-label">碳水</div>
              <div class="nutrition-value">{{ result.food.carbohydrate }}</div>
              <div class="nutrition-unit">g</div>
            </div>
          </div>

          <!-- 建议分量 -->
          <div class="suggested-portion" v-if="result.food.suggestedPortion">
            <van-icon name="info-o" />
            <span>建议分量: {{ result.food.suggestedPortion }}{{ result.food.unit }}</span>
          </div>
        </div>

        <!-- AI营养分析 -->
        <div class="ai-analysis" v-if="result.nutritionAnalysis">
          <div class="analysis-title">
            <van-icon name="bulb-o" />
            <span>AI营养分析</span>
          </div>
          <div class="analysis-content">
            {{ result.nutritionAnalysis }}
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <van-button
            round
            block
            type="primary"
            icon="plus"
            @click="addToDiary"
          >
            添加到饮食日记
          </van-button>
          <van-button
            round
            block
            plain
            type="primary"
            icon="photograph"
            @click="resetAnalysis"
            class="re-upload-btn"
          >
            重新上传
          </van-button>
        </div>
      </div>

      <!-- 历史记录 -->
      <div class="history-section" v-if="!result && !analyzing">
        <div class="section-title">
          <van-icon name="clock-o" />
          <span>最近识别</span>
        </div>
        <div class="history-list">
          <div
            v-for="item in historyList"
            :key="item.id"
            class="history-item"
            @click="viewHistoryDetail(item)"
          >
            <div class="history-icon">
              <van-icon name="food-o" size="24" color="#1989fa" />
            </div>
            <div class="history-info">
              <div class="history-name">{{ item.foodName }}</div>
              <div class="history-meta">
                <span>{{ item.calories }}千卡</span>
                <span class="time-dot">·</span>
                <span>{{ formatTime(item.createdAt) }}</span>
              </div>
            </div>
            <van-icon name="arrow" color="#c8c9cc" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { analyzeFoodImageUpload } from '@/api/food'

const router = useRouter()

const fileList = ref([])
const analyzing = ref(false)
const result = ref(null)
const historyList = ref([])

// 组件挂载时加载历史记录并清理旧数据
onMounted(() => {
  cleanupOldHistory()
  loadHistory()
})

// 清理旧版本的包含Base64图片的历史记录
function cleanupOldHistory() {
  try {
    const history = JSON.parse(localStorage.getItem('foodAnalysisHistory') || '[]')
    
    // 检查是否有包含imageUrl的旧记录（Base64图片）
    const hasOldFormat = history.some(item => item.imageUrl || (item.data && item.data.imageUrl))
    
    if (hasOldFormat) {
      console.log('检测到旧格式历史记录，清理中...')
      localStorage.removeItem('foodAnalysisHistory')
    }
  } catch (error) {
    // 如果解析失败，直接清空
    console.warn('历史记录格式错误，清空:', error)
    localStorage.removeItem('foodAnalysisHistory')
  }
}

// 文件读取前校验
function beforeRead(file) {
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    showToast('请上传图片文件')
    return false
  }
  
  // 检查文件大小（最大10MB）
  if (file.size > 10 * 1024 * 1024) {
    showToast('图片大小不能超过10MB')
    return false
  }
  
  return true
}

// 文件读取后
async function afterRead(file) {
  console.log('开始分析食物图片:', file)
  analyzing.value = true
  result.value = null

  try {
    // 创建临时URL用于当前会话显示，不保存到localStorage
    const imageUrl = URL.createObjectURL(file.file)
    
    const data = await analyzeFoodImageUpload(file.file)
    console.log('AI分析结果:', data)
    
    if (data && data.success && data.food) {
      // 将临时图片URL添加到结果中
      data.imageUrl = imageUrl
      result.value = data
      
      // 保存到历史记录（不包含图片）
      saveToHistory(data)
    } else {
      const errorMsg = data?.errorMessage || '无法识别该图片，请确保图片清晰且包含食物'
      showDialog({
        title: '识别失败',
        message: errorMsg
      })
      // 清空文件列表
      fileList.value = []
      // 释放临时URL
      URL.revokeObjectURL(imageUrl)
    }
  } catch (error) {
    console.error('AI分析失败:', error)
    showDialog({
      title: '识别失败',
      message: error.message || '网络错误，请重试'
    })
    // 清空文件列表
    fileList.value = []
  } finally {
    analyzing.value = false
  }
}

// 保存到历史记录（不包含图片，避免localStorage超出配额）
function saveToHistory(data) {
  try {
    const history = JSON.parse(localStorage.getItem('foodAnalysisHistory') || '[]')
    
    // 只保存必要的食物信息，不保存Base64图片
    history.unshift({
      id: Date.now(),
      foodName: data.food.name,
      category: data.food.category,
      calories: data.food.calories,
      protein: data.food.protein,
      fat: data.food.fat,
      carbohydrate: data.food.carbohydrate,
      // 不保存imageUrl，避免localStorage超出配额
      createdAt: new Date().toISOString()
    })
    
    // 只保留最近10条
    if (history.length > 10) {
      history.length = 10
    }
    
    localStorage.setItem('foodAnalysisHistory', JSON.stringify(history))
    loadHistory()
  } catch (error) {
    // 如果localStorage仍然超出配额，清空历史记录
    console.warn('保存历史记录失败，清空历史记录:', error)
    localStorage.removeItem('foodAnalysisHistory')
  }
}

// 加载历史记录
function loadHistory() {
  const history = JSON.parse(localStorage.getItem('foodAnalysisHistory') || '[]')
  historyList.value = history
}

// 格式化时间
function formatTime(datetime) {
  const date = new Date(datetime)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

// 查看历史详情
function viewHistoryDetail(item) {
  // 重构历史记录数据格式以适配result
  result.value = {
    success: true,
    food: {
      name: item.foodName,
      category: item.category,
      calories: item.calories,
      protein: item.protein,
      fat: item.fat,
      carbohydrate: item.carbohydrate
    }
  }
  // 历史记录没有图片，不设置fileList
}

// 重置分析
function resetAnalysis() {
  result.value = null
  fileList.value = []
}

// 添加到饮食日记
function addToDiary() {
  if (!result.value || !result.value.food) {
    showToast('食物信息不完整')
    return
  }
  
  console.log('跳转到饮食日记，食物信息:', result.value.food)
  
  // 如果有foodId，传递foodId
  if (result.value.food.id) {
    router.push({
      name: 'DietAdd',
      query: {
        foodId: result.value.food.id,
        foodName: result.value.food.name,
        from: 'analysis'
      }
    })
  } else {
    // 如果没有foodId，只传递食物名称，让用户在添加页面搜索
    showToast('请在添加页面搜索该食物')
    router.push({
      name: 'DietAdd',
      query: {
        foodName: result.value.food.name,
        from: 'analysis'
      }
    })
  }
}

// 加载历史记录
loadHistory()
</script>

<style scoped>
.food-analysis-page {
  padding-top: 46px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.content {
  padding: 16px;
}

.upload-section {
  margin-bottom: 16px;
}

.upload-area {
  background: white;
  border: 2px dashed #ddd;
  border-radius: 6px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:active {
  background-color: #f7f8fa;
  border-color: #1989fa;
}

.upload-text {
  font-size: 12px;
  color: #333;
  margin: 8px 0 4px;
  font-weight: 500;
}

.upload-hint {
  font-size: 11px;
  color: #999;
}

.analyzing {
  text-align: center;
  padding: 60px 20px;
  color: #1989fa;
}

.result-section {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.result-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}

.result-title {
  font-size: 13px;
  font-weight: 600;
  color: #52c41a;
}

.food-info-card {
  background: white;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.food-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 4px;
}

.food-category {
  font-size: 11px;
  color: #999;
  text-align: center;
  margin-bottom: 10px;
}

.nutrition-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
  margin-bottom: 10px;
}

.nutrition-item {
  text-align: center;
}

.nutrition-label {
  font-size: 10px;
  color: #999;
  margin-bottom: 3px;
}

.nutrition-value {
  font-size: 14px;
  font-weight: bold;
  color: #4a90e2;
}

.nutrition-unit {
  font-size: 9px;
  color: #666;
  margin-top: 1px;
}

.suggested-portion {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px;
  background-color: #f0f5ff;
  border-radius: 4px;
  font-size: 11px;
  color: #667eea;
}

.ai-analysis {
  background: white;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.analysis-title {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.analysis-content {
  font-size: 12px;
  line-height: 1.4;
  color: #666;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.re-upload-btn {
  margin-top: 8px;
}

.history-section {
  margin-top: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-item:active {
  background: #f7f8fa;
  transform: scale(0.98);
}

.history-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e8f4ff;
  border-radius: 8px;
}

.history-info {
  flex: 1;
  min-width: 0;
}

.history-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-meta {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.time-dot {
  color: #ddd;
}
</style>
