<template>
  <div class="knowledge-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="营养知识" left-arrow @click-left="$router.back()" fixed />

    <div class="content">
      <!-- 搜索栏 -->
      <van-search
        v-model="searchText"
        placeholder="搜索营养知识"
        @search="onSearch"
      />

      <!-- 分类标签 -->
      <div class="category-tabs">
        <van-button
          v-for="category in categories"
          :key="category.value"
          :type="selectedCategory === category.value ? 'primary' : 'default'"
          size="small"
          round
          @click="selectCategory(category.value)"
        >
          {{ category.label }}
        </van-button>
      </div>

      <!-- 知识列表 -->
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadKnowledgeList"
      >
        <div
          v-for="item in knowledgeList"
          :key="item.id"
          class="knowledge-card"
          @click="viewDetail(item)"
        >
          <div class="card-header">
            <van-tag :type="getCategoryTagType(item.category)">
              {{ getCategoryLabel(item.category) }}
            </van-tag>
            <span class="read-count">
              <van-icon name="eye-o" />
              {{ item.readCount }}
            </span>
          </div>
          
          <div class="card-title">{{ item.title }}</div>
          
          <div class="card-summary">{{ item.summary }}</div>
          
          <div class="card-footer">
            <span class="publish-time">{{ formatTime(item.publishTime) }}</span>
            <van-icon name="arrow" />
          </div>
        </div>
      </van-list>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && knowledgeList.length === 0"
        description="暂无相关知识"
        image="search"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

const categories = [
  { label: '全部', value: 'ALL' },
  { label: '营养基础', value: 'BASIC' },
  { label: '食材百科', value: 'FOOD' },
  { label: '健康食谱', value: 'RECIPE' },
  { label: '减肥指南', value: 'WEIGHT_LOSS' },
  { label: '运动营养', value: 'SPORTS' }
]

const searchText = ref('')
const selectedCategory = ref('ALL')
const knowledgeList = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 10

// 模拟数据（实际项目中应调用后端API）
const mockData = [
  {
    id: 1,
    title: '蛋白质的重要性及每日推荐摄入量',
    summary: '蛋白质是生命的基础，参与人体各种生理功能。了解如何合理摄入蛋白质对健康至关重要。',
    category: 'BASIC',
    readCount: 1250,
    publishTime: '2024-01-15T10:00:00'
  },
  {
    id: 2,
    title: '鸡胸肉的营养价值与烹饪方法',
    summary: '鸡胸肉是优质蛋白质来源，低脂高蛋白，适合健身减脂人群。本文介绍多种美味烹饪方法。',
    category: 'FOOD',
    readCount: 980,
    publishTime: '2024-01-14T15:30:00'
  },
  {
    id: 3,
    title: '健康减脂餐食谱推荐',
    summary: '科学搭配一周减脂餐，既能保证营养又能有效控制热量，轻松享瘦健康生活。',
    category: 'RECIPE',
    readCount: 2100,
    publishTime: '2024-01-13T09:00:00'
  },
  {
    id: 4,
    title: '如何科学制定减肥计划',
    summary: '减肥不是简单的少吃多动，需要科学的方法和合理的规划。本文教你制定适合自己的减肥计划。',
    category: 'WEIGHT_LOSS',
    readCount: 1850,
    publishTime: '2024-01-12T14:20:00'
  },
  {
    id: 5,
    title: '运动前后的营养补充指南',
    summary: '合理的运动前后营养补充能提升运动表现，加速恢复，避免肌肉流失。',
    category: 'SPORTS',
    readCount: 1320,
    publishTime: '2024-01-11T16:45:00'
  },
  {
    id: 6,
    title: '碳水化合物的作用与选择',
    summary: '碳水化合物是人体主要能量来源，但要学会区分优质碳水和劣质碳水，做出健康选择。',
    category: 'BASIC',
    readCount: 1100,
    publishTime: '2024-01-10T11:30:00'
  },
  {
    id: 7,
    title: '牛油果：超级食物的营养解析',
    summary: '牛油果富含健康脂肪和多种维生素，被誉为超级食物。了解其营养价值和食用方法。',
    category: 'FOOD',
    readCount: 890,
    publishTime: '2024-01-09T13:15:00'
  },
  {
    id: 8,
    title: '地中海饮食：健康长寿的秘诀',
    summary: '地中海饮食被公认为最健康的饮食模式之一，富含橄榄油、鱼类、蔬果等营养食材。',
    category: 'RECIPE',
    readCount: 1450,
    publishTime: '2024-01-08T10:00:00'
  }
]

// 选择分类
function selectCategory(category) {
  if (selectedCategory.value !== category) {
    selectedCategory.value = category
    knowledgeList.value = []
    page.value = 1
    finished.value = false
    loadKnowledgeList()
  }
}

// 搜索
function onSearch() {
  knowledgeList.value = []
  page.value = 1
  finished.value = false
  loadKnowledgeList()
}

// 加载知识列表
async function loadKnowledgeList() {
  if (loading.value) return

  loading.value = true
  
  // 模拟API调用延迟
  setTimeout(() => {
    try {
      // 过滤数据
      let filteredData = [...mockData]
      
      if (selectedCategory.value !== 'ALL') {
        filteredData = filteredData.filter(item => item.category === selectedCategory.value)
      }
      
      if (searchText.value) {
        filteredData = filteredData.filter(item => 
          item.title.includes(searchText.value) || item.summary.includes(searchText.value)
        )
      }
      
      // 分页
      const start = (page.value - 1) * pageSize
      const end = start + pageSize
      const newItems = filteredData.slice(start, end)
      
      knowledgeList.value = [...knowledgeList.value, ...newItems]
      
      if (newItems.length < pageSize || end >= filteredData.length) {
        finished.value = true
      } else {
        page.value++
      }
    } catch (error) {
      showToast('加载失败')
    } finally {
      loading.value = false
    }
  }, 500)
}

// 查看详情
function viewDetail(item) {
  showToast('知识详情页面开发中...')
  // 实际项目中应跳转到详情页
  // router.push({ name: 'KnowledgeDetail', params: { id: item.id } })
}

// 获取分类标签类型
function getCategoryTagType(category) {
  const typeMap = {
    'BASIC': 'primary',
    'FOOD': 'success',
    'RECIPE': 'warning',
    'WEIGHT_LOSS': 'danger',
    'SPORTS': 'default'
  }
  return typeMap[category] || 'default'
}

// 获取分类标签
function getCategoryLabel(category) {
  const labelMap = {
    'BASIC': '营养基础',
    'FOOD': '食材百科',
    'RECIPE': '健康食谱',
    'WEIGHT_LOSS': '减肥指南',
    'SPORTS': '运动营养'
  }
  return labelMap[category] || category
}

// 格式化时间
function formatTime(datetime) {
  const date = new Date(datetime)
  const now = new Date()
  const diff = now - date
  
  if (diff < 86400000) {
    return '今天'
  } else if (diff < 172800000) {
    return '昨天'
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}
</script>

<style scoped>
.knowledge-page {
  padding-top: 46px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.content {
  padding: 12px;
}

.category-tabs {
  display: flex;
  gap: 6px;
  padding: 8px 0;
  overflow-x: auto;
  white-space: nowrap;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.knowledge-card {
  background: white;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.knowledge-card:active {
  transform: scale(0.98);
  background-color: #fafafa;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.read-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.4;
}

.card-summary {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.publish-time {
  font-size: 12px;
  color: #999;
}
</style>
