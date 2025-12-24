<template>
  <div class="goal-settings-page">
    <van-nav-bar title="目标设置" left-arrow @click-left="$router.back()" fixed />

    <div class="content">
      <van-form @submit="onSubmit">
        <!-- 基本信息 -->
        <div class="section">
          <div class="section-title">基本信息</div>
          <van-cell-group inset>
            <van-field
              v-model="formData.age"
              type="number"
              label="年龄"
              placeholder="请输入年龄"
              :rules="[{ required: true, message: '请输入年龄' }]"
              @blur="calculateRecommendation"
            />
            <van-field
              v-model="formData.height"
              type="number"
              label="身高"
              placeholder="请输入身高(cm)"
              :rules="[{ required: true, message: '请输入身高' }]"
              @blur="calculateRecommendation"
            />
            <van-field
              v-model="formData.initialWeight"
              type="number"
              label="初始体重"
              placeholder="请输入开始时的体重(kg)"
              :rules="[{ required: true, message: '请输入初始体重' }]"
              @blur="calculateRecommendation"
            />
            <div class="field-tip">初始体重用于计算目标达成进度，每日体重请在"体重记录"中添加</div>
            <van-field
              name="gender"
              label="性别"
              placeholder="点击选择性别"
              readonly
              clickable
              :model-value="genderText"
              @click="showGenderPicker = true"
            />
          </van-cell-group>
        </div>

        <!-- 目标设置 -->
        <div class="section">
          <div class="section-title">健康目标</div>
          <van-cell-group inset>
            <van-field
              v-model="formData.targetWeight"
              type="number"
              label="目标体重"
              placeholder="请输入目标体重(kg)"
              @blur="calculateRecommendation"
            />
            <van-field
              v-model="formData.targetCalories"
              type="number"
              label="每日热量目标"
              placeholder="建议1500-2500千卡"
            />
            <van-field
              v-model="formData.targetProtein"
              type="number"
              label="每日蛋白质"
              placeholder="建议50-100g"
            />
            <van-field
              name="activityLevel"
              label="活动水平"
              placeholder="点击选择活动水平"
              readonly
              clickable
              :model-value="activityLevelText"
              @click="showActivityPicker = true"
            />
          </van-cell-group>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-container">
          <van-button round block type="primary" native-type="submit" :loading="submitting">
            保存设置
          </van-button>
        </div>
      </van-form>

      <!-- 智能推荐 -->
      <div class="ai-recommendation" v-if="recommendation">
        <div class="rec-title">
          <van-icon name="bulb-o" />
          AI推荐
        </div>
        <div class="rec-content">{{ recommendation }}</div>
      </div>
    </div>

    <!-- 性别选择器 -->
    <van-popup v-model:show="showGenderPicker" position="bottom">
      <van-picker
        :columns="genderOptions"
        @confirm="onGenderConfirm"
        @cancel="showGenderPicker = false"
      />
    </van-popup>

    <!-- 活动水平选择器 -->
    <van-popup v-model:show="showActivityPicker" position="bottom">
      <van-picker
        :columns="activityOptions"
        @confirm="onActivityConfirm"
        @cancel="showActivityPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
// 体重记录请在体重管理页面操作
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

const formData = ref({
  age: '',
  height: '',
  initialWeight: '',
  gender: 'MALE',
  targetWeight: '',
  targetCalories: '2000',
  targetProtein: '80',
  activityLevel: 'MODERATE'
})

const showGenderPicker = ref(false)
const showActivityPicker = ref(false)
const submitting = ref(false)
const recommendation = ref('')

const genderOptions = [
  { text: '男', value: 'MALE' },
  { text: '女', value: 'FEMALE' }
]

const activityOptions = [
  { text: '久坐不动', value: 'SEDENTARY' },
  { text: '轻度活动', value: 'LIGHT' },
  { text: '中度活动', value: 'MODERATE' },
  { text: '高度活动', value: 'ACTIVE' },
  { text: '极高活动', value: 'VERY_ACTIVE' }
]

const genderText = computed(() => {
  const option = genderOptions.find(o => o.value === formData.value.gender)
  return option ? option.text : ''
})

const activityLevelText = computed(() => {
  const option = activityOptions.find(o => o.value === formData.value.activityLevel)
  return option ? option.text : ''
})

function onGenderConfirm(selection) {
  formData.value.gender = selection.selectedValues[0]
  showGenderPicker.value = false
  calculateRecommendation()
}

function onActivityConfirm(selection) {
  formData.value.activityLevel = selection.selectedValues[0]
  showActivityPicker.value = false
  calculateRecommendation()
}

// 计算AI推荐
function calculateRecommendation() {
  if (!formData.value.age || !formData.value.height || !formData.value.initialWeight) {
    return
  }

  const age = parseInt(formData.value.age)
  const height = parseFloat(formData.value.height)
  const weight = parseFloat(formData.value.initialWeight)
  const isMale = formData.value.gender === 'MALE'

  // 计算BMI
  const bmi = (weight / Math.pow(height / 100, 2)).toFixed(1)

  // 计算基础代谢率 (Harris-Benedict公式)
  let bmr
  if (isMale) {
    bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
  } else {
    bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
  }

  // 根据活动水平调整
  const activityMultipliers = {
    'SEDENTARY': 1.2,
    'LIGHT': 1.375,
    'MODERATE': 1.55,
    'ACTIVE': 1.725,
    'VERY_ACTIVE': 1.9
  }
  
  const tdee = Math.round(bmr * (activityMultipliers[formData.value.activityLevel] || 1.55))

  let bmiStatus = ''
  if (bmi < 18.5) bmiStatus = '偏瘦'
  else if (bmi < 24) bmiStatus = '正常'
  else if (bmi < 28) bmiStatus = '超重'
  else bmiStatus = '肥胖'

  recommendation.value = `根据您的身体数据，您的BMI为${bmi}(${bmiStatus})，建议每日摄入${tdee}千卡。蛋白质建议摄入${Math.round(weight * 1.2)}g，以维持肌肉量。`
  
  // 自动填充推荐值
  formData.value.targetCalories = tdee.toString()
  formData.value.targetProtein = Math.round(weight * 1.2).toString()
}

async function onSubmit() {
  try {
    submitting.value = true
    
    // 保存到本地存储
    localStorage.setItem('userGoals', JSON.stringify(formData.value))
    
    showToast('目标设置成功')
    
    setTimeout(() => {
      router.back()
    }, 500)
  } catch (error) {
    console.error('保存失败:', error)
    showToast('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  // 加载已保存的设置
  const saved = localStorage.getItem('userGoals')
  if (saved) {
    try {
      formData.value = JSON.parse(saved)
      calculateRecommendation()
    } catch (e) {
      console.error('加载设置失败:', e)
    }
  }
  // 初始体重由用户手动输入，用于计算目标完成度
})
</script>

<style scoped>
.goal-settings-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
  padding-bottom: 20px;
}

.content {
  padding: 12px;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #323233;
  padding: 8px 12px;
}

.submit-container {
  padding: 20px 12px;
}

.ai-recommendation {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 12px;
  margin: 12px 0;
  color: white;
}

.rec-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.rec-content {
  font-size: 12px;
  line-height: 1.6;
  opacity: 0.95;
}

.field-tip {
  font-size: 12px;
  color: #969799;
  padding: 8px 16px 12px;
  line-height: 1.5;
}
</style>
