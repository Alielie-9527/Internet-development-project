<template>
  <div class="diet-add-page">
    <!-- 顶部导航 -->
    <van-nav-bar :title="isEdit ? '编辑记录' : '添加饮食'" left-arrow @click-left="handleBack" fixed />

    <div class="form-container">
      <van-form @submit.prevent="onSubmit">
        <!-- 餐次选择 -->
        <van-field
          v-model="mealTypeName"
          label="餐次"
          placeholder="选择餐次"
          readonly
          right-icon="arrow"
          @click="showMealTypePicker = true"
        />

        <!-- 食物选择 -->
        <van-field
          v-model="foodName"
          label="食物"
          placeholder="搜索或选择食物"
          right-icon="search"
          @click="showFoodSearch = true"
          readonly
        />

        <!-- 食物营养信息展示 -->
        <div v-if="selectedFood" class="food-nutrition">
          <div class="nutrition-item">
            <span class="label">热量:</span>
            <span class="value">{{ selectedFood.calories }}千卡/{{ selectedFood.unit }}</span>
          </div>
          <div class="nutrition-item">
            <span class="label">蛋白质:</span>
            <span class="value">{{ selectedFood.protein }}g</span>
          </div>
          <div class="nutrition-item">
            <span class="label">脂肪:</span>
            <span class="value">{{ selectedFood.fat }}g</span>
          </div>
          <div class="nutrition-item">
            <span class="label">碳水:</span>
            <span class="value">{{ selectedFood.carbohydrate }}g</span>
          </div>
        </div>

        <!-- 分量 -->
        <van-field
          v-model="formData.portion"
          type="number"
          label="分量"
          placeholder="输入分量"
        >
          <template #button>
            <span class="unit-text">{{ selectedFood?.unit || '份' }}</span>
          </template>
        </van-field>

        <!-- 用餐时间 -->
        <van-field
          v-model="mealTimeDisplay"
          label="用餐时间"
          placeholder="选择时间"
          readonly
          right-icon="clock-o"
          @click="showTimePicker = true"
        />

        <!-- 备注 -->
        <van-field
          v-model="formData.notes"
          label="备注"
          type="textarea"
          placeholder="添加备注（可选）"
          rows="3"
          maxlength="200"
          show-word-limit
        />

        <!-- 提交按钮 -->
        <div class="submit-container">
          <van-button round block type="primary" @click="onSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '添加记录' }}
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 餐次选择器 -->
    <van-popup v-model:show="showMealTypePicker" position="bottom">
      <van-picker
        :columns="mealTypes"
        @confirm="onMealTypeConfirm"
        @cancel="showMealTypePicker = false"
      />
    </van-popup>

    <!-- 时间选择器 -->
    <van-popup v-model:show="showTimePicker" position="bottom">
      <van-date-picker
        v-model="mealTimeValue"
        type="datetime"
        :min-date="new Date(2020, 0, 1)"
        :max-date="new Date()"
        @confirm="onTimeConfirm"
        @cancel="showTimePicker = false"
      />
    </van-popup>

    <!-- 食物搜索弹窗 -->
    <van-popup v-model:show="showFoodSearch" position="bottom" :style="{ height: '70%' }">
      <div class="food-search-container">
        <van-search
          v-model="searchKeyword"
          placeholder="搜索食物，如：米饭、苹果"
          show-action
          @search="onSearch"
          @update:model-value="onSearchInput"
        >
          <template #action>
            <div @click="onSearch">搜索</div>
          </template>
        </van-search>
        <div class="food-list">
          <van-loading v-if="searching" type="spinner" size="24">搜索中...</van-loading>
          <template v-else>
            <div
              v-for="food in foodList"
              :key="food.id"
              class="food-item"
              @click="selectFood(food)"
            >
              <div class="food-info-left">
                <div class="food-name">{{ food.name }}</div>
                <div class="food-nutrition">
                  {{ food.calories }}千卡/{{ food.unit }}
                  <span class="separator">·</span>
                  蛋白{{ food.protein }}g
                </div>
              </div>
              <van-icon name="plus" class="add-icon" />
            </div>
            <div v-if="foodList.length === 0 && !searching" class="empty-hint">
              <van-empty description="请输入关键词搜索食物" />
              <div class="search-tips">
                <p>搜索建议：</p>
                <p>• 输入食物名称，如"鸡蛋"、"苹果"</p>
                <p>• 支持模糊搜索</p>
              </div>
            </div>
          </template>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { addDietDiary, updateDietDiary, getDietDiaryDetail } from '@/api/diet'
import { searchFood } from '@/api/food'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => route.name === 'DietEdit' || !!route.params.id)

// 返回处理
function handleBack() {
  console.log('点击返回按钮')
  // 清空搜索状态
  clearSearch()
  router.back()
}

// 表单数据
const formData = ref({
  mealType: 'BREAKFAST',
  foodId: null,
  portion: '1',
  mealTime: new Date(),
  notes: ''
})

// 选择的食物
const selectedFood = ref(null)
const foodName = computed(() => selectedFood.value?.name || '')

// 餐次
const mealTypes = [
  { text: '早餐', value: 'BREAKFAST' },
  { text: '午餐', value: 'LUNCH' },
  { text: '晚餐', value: 'DINNER' },
  { text: '加餐', value: 'SNACK' }
]
const mealTypeName = ref('早餐')
const showMealTypePicker = ref(false)

// 时间
const mealTimeValue = ref([])
const mealTimeDisplay = computed(() => {
  const date = formData.value.mealTime
  if (!date) return ''
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const targetDate = new Date(date)
  targetDate.setHours(0, 0, 0, 0)
  
  let dateLabel = ''
  if (targetDate.getTime() === today.getTime()) {
    dateLabel = '今天'
  } else {
    const yesterday = new Date(today)
    yesterday.setDate(yesterday.getDate() - 1)
    if (targetDate.getTime() === yesterday.getTime()) {
      dateLabel = '昨天'
    } else {
      dateLabel = `${date.getMonth() + 1}月${date.getDate()}日`
    }
  }
  
  return `${dateLabel} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
})
const showTimePicker = ref(false)

// 食物搜索
const showFoodSearch = ref(false)
const searchKeyword = ref('')
const foodList = ref([])
const searching = ref(false)

const submitting = ref(false)

// 搜索输入变化（用于实时搜索）
let searchTimer = null
function onSearchInput(value) {
  if (!value || value.trim().length < 2) {
    foodList.value = []
    return
  }
  
  // 防抖搜索
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  searchTimer = setTimeout(() => {
    onSearch()
  }, 500)
}

// 搜索食物
async function onSearch() {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    showToast('请输入关键词')
    return
  }

  if (keyword.length < 2) {
    showToast('请至少输入2个字符')
    return
  }

  searching.value = true
  try {
    const result = await searchFood(keyword, 1, 50)
    console.log('搜索食物结果:', result)
    if (result && result.list) {
      foodList.value = result.list || []
      if (foodList.value.length === 0) {
        showToast('未找到相关食物')
      }
    } else if (Array.isArray(result)) {
      foodList.value = result
      if (foodList.value.length === 0) {
        showToast('未找到相关食物')
      }
    } else {
      foodList.value = []
      showToast('未找到相关食物')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    showToast('搜索失败，请重试')
    foodList.value = []
  } finally {
    searching.value = false
  }
}

// 选择食物
function selectFood(food) {
  console.log('选择食物:', food)
  selectedFood.value = {
    id: food.id,
    name: food.name,
    calories: food.calories,
    protein: food.protein,
    fat: food.fat,
    carbohydrate: food.carbohydrate,
    unit: food.unit
  }
  formData.value.foodId = food.id
  // 如果分量为空，设置默认值为1
  if (!formData.value.portion || formData.value.portion === '0') {
    formData.value.portion = '1'
  }
  showFoodSearch.value = false
  console.log('当前表单数据:', formData.value)
}

// 餐次确认
function onMealTypeConfirm({ selectedOptions }) {
  formData.value.mealType = selectedOptions[0].value
  mealTypeName.value = selectedOptions[0].text
  showMealTypePicker.value = false
}

// 时间确认
function onTimeConfirm(value) {
  formData.value.mealTime = value
  showTimePicker.value = false
}

// 提交表单
async function onSubmit() {
  console.log('提交表单，当前表单数据:', formData.value)
  
  if (!formData.value.foodId) {
    showToast('请选择食物')
    return
  }

  if (!formData.value.portion || formData.value.portion <= 0) {
    showToast('请输入有效分量')
    return
  }

  submitting.value = true
  try {
    const data = {
      mealType: formData.value.mealType,
      foodId: formData.value.foodId,
      portion: Number(formData.value.portion),
      mealTime: formData.value.mealTime.toISOString(),
      notes: formData.value.notes || ''
    }
    
    console.log('准备提交的数据:', data)

    if (isEdit.value) {
      data.id = Number(route.params.id)
      await updateDietDiary(data)
      showToast('修改成功')
      // 触发饮食记录更新事件
      window.dispatchEvent(new Event('diet-updated'))
      router.back()
    } else {
      await addDietDiary(data)
      showToast('添加成功')
      // 触发饮食记录更新事件
      window.dispatchEvent(new Event('diet-updated'))
      router.back()
    }
  } catch (error) {
    console.error('提交失败:', error)
    showToast(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 加载详情（编辑模式）
async function loadDetail() {
  if (!isEdit.value) return

  try {
    const data = await getDietDiaryDetail(route.params.id)
    console.log('加载到的详情数据:', data)
    
    if (data) {
      formData.value = {
        mealType: data.mealType,
        foodId: data.foodId,
        portion: String(data.portion),
        mealTime: new Date(data.mealTime),
        notes: data.notes || ''
      }
      
      selectedFood.value = {
        id: data.foodId,
        name: data.foodName,
        calories: data.calories,
        protein: data.protein,
        fat: data.fat,
        carbohydrate: data.carbohydrate,
        unit: data.unit
      }

      const mealType = mealTypes.find(t => t.value === data.mealType)
      if (mealType) {
        mealTypeName.value = mealType.text
      }
      
      // 更新时间选择器
      const date = formData.value.mealTime
      mealTimeValue.value = [
        date.getFullYear(),
        date.getMonth() + 1,
        date.getDate(),
        date.getHours(),
        date.getMinutes()
      ]
    } else {
      showToast('加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载详情失败:', error)
    showToast('加载失败')
    router.back()
  }
}

// 清除搜索
function clearSearch() {
  searchKeyword.value = ''
  foodList.value = []
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
}

onMounted(() => {
  // 设置初始日期（从路由参数获取）
  if (route.query.date) {
    // 从列表页传来的日期（格式：YYYY-MM-DD）
    const dateStr = route.query.date
    const dateParts = dateStr.split('-')
    const year = parseInt(dateParts[0])
    const month = parseInt(dateParts[1]) - 1
    const day = parseInt(dateParts[2])
    
    // 创建日期，使用当前时间
    const now = new Date()
    formData.value.mealTime = new Date(year, month, day, now.getHours(), now.getMinutes())
    console.log('从路由获取日期:', dateStr, '设置为:', formData.value.mealTime)
  }
  
  // 从食物分析页面跳转过来的情况
  if (route.query.foodId && route.query.foodName) {
    selectedFood.value = {
      id: parseInt(route.query.foodId),
      name: route.query.foodName
    }
    formData.value.foodId = parseInt(route.query.foodId)
  }
  
  // 设置时间选择器初始值
  const targetDate = formData.value.mealTime
  mealTimeValue.value = [
    targetDate.getFullYear(),
    targetDate.getMonth() + 1,
    targetDate.getDate(),
    targetDate.getHours(),
    targetDate.getMinutes()
  ]

  loadDetail()
})

// 监听弹窗关闭，清空搜索
watch(showFoodSearch, (newVal) => {
  if (!newVal) {
    clearSearch()
  }
})
</script>

<style scoped>
.diet-add-page {
  padding-top: 46px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.form-container {
  padding: 16px;
}

.food-nutrition {
  background: white;
  padding: 16px;
  margin: 8px 0;
  border-radius: 8px;
}

.nutrition-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.nutrition-item .label {
  color: #666;
}

.nutrition-item .value {
  color: #333;
  font-weight: 500;
}

.unit-text {
  color: #666;
  font-size: 14px;
}

.submit-container {
  margin-top: 32px;
}

.food-search-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f7f8fa;
}

.food-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 16px;
}

.food-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: white;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.food-item:active {
  background-color: #e8f4ff;
  transform: scale(0.98);
}

.food-info-left {
  flex: 1;
  min-width: 0;
}

.food-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.food-nutrition {
  font-size: 12px;
  color: #999;
}

.separator {
  margin: 0 6px;
  color: #ddd;
}

.add-icon {
  color: #1989fa;
  font-size: 20px;
  flex-shrink: 0;
}

.empty-hint {
  text-align: center;
  padding: 40px 20px;
}

.search-tips {
  margin-top: 20px;
  text-align: left;
  background: white;
  padding: 16px;
  border-radius: 8px;
}

.search-tips p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.search-tips p:first-child {
  font-weight: 600;
  color: #333;
}
</style>
