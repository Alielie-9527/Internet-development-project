<template>
  <div class="assessment-page">
    <van-nav-bar title="健康评估" left-arrow @click-left="$router.back()" fixed />

    <div class="content">
      <!-- 评估卡片列表 -->
      <div class="assessment-cards">
        <div class="card" @click="startAssessment('bmi')">
          <div class="card-icon">
            <van-icon name="balance-o" size="28" color="#52c41a" />
          </div>
          <div class="card-content">
            <h3>BMI评估</h3>
            <p>评估体重指数，了解身体状况</p>
            <van-tag type="success" v-if="assessmentResults.bmi">已完成</van-tag>
          </div>
          <van-icon name="arrow" class="arrow" />
        </div>

        <div class="card" @click="startAssessment('nutrition')">
          <div class="card-icon">
            <van-icon name="bar-chart-o" size="28" color="#faad14" />
          </div>
          <div class="card-content">
            <h3>营养评估</h3>
            <p>分析饮食结构，优化营养摄入</p>
            <van-tag type="warning" v-if="assessmentResults.nutrition">已完成</van-tag>
          </div>
          <van-icon name="arrow" class="arrow" />
        </div>

        <div class="card" @click="startAssessment('health')">
          <div class="card-icon">
            <van-icon name="like-o" size="28" color="#ff6b6b" />
          </div>
          <div class="card-content">
            <h3>健康习惯评估</h3>
            <p>评估生活习惯，提供改善建议</p>
            <van-tag type="danger" v-if="assessmentResults.health">已完成</van-tag>
          </div>
          <van-icon name="arrow" class="arrow" />
        </div>

        <div class="card" @click="startAssessment('lifestyle')">
          <div class="card-icon">
            <van-icon name="fire-o" size="28" color="#667eea" />
          </div>
          <div class="card-content">
            <h3>运动水平评估</h3>
            <p>评估活动量，制定运动计划</p>
            <van-tag type="primary" v-if="assessmentResults.lifestyle">已完成</van-tag>
          </div>
          <van-icon name="arrow" class="arrow" />
        </div>
      </div>

      <!-- 历史记录 -->
      <div class="history-section" v-if="hasHistory">
        <div class="section-title">评估历史</div>
        <div class="history-list">
          <div 
            v-for="record in historyRecords"
            :key="record.id"
            class="history-item"
            @click="viewHistory(record)"
          >
            <div class="history-info">
              <div class="history-name">{{ record.name }}</div>
              <div class="history-date">{{ record.date }}</div>
            </div>
            <div class="history-score">
              <span class="score-value" :class="getScoreClass(record.score)">
                {{ record.score }}
              </span>
              <span class="score-label">分</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

const assessmentResults = ref({
  bmi: null,
  nutrition: null,
  health: null,
  lifestyle: null
})

const historyRecords = ref([])

const hasHistory = computed(() => historyRecords.value.length > 0)

function startAssessment(type) {
  const typeMap = {
    'bmi': 'BMI评估',
    'nutrition': '营养评估',
    'health': '健康习惯评估',
    'lifestyle': '运动水平评估'
  }

  showToast({
    message: `${typeMap[type]}功能开发中，敬请期待`,
    duration: 2000
  })
}

function viewHistory(record) {
  showToast('查看历史详情')
}

function getScoreClass(score) {
  if (score >= 80) return 'excellent'
  if (score >= 60) return 'good'
  return 'poor'
}

onMounted(() => {
  const saved = localStorage.getItem('assessmentResults')
  if (saved) {
    try {
      assessmentResults.value = JSON.parse(saved)
    } catch (e) {
      console.error('加载评估结果失败:', e)
    }
  }

  const savedHistory = localStorage.getItem('assessmentHistory')
  if (savedHistory) {
    try {
      historyRecords.value = JSON.parse(savedHistory)
    } catch (e) {
      console.error('加载历史记录失败:', e)
    }
  }
})
</script>

<style scoped>
.assessment-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
  padding-bottom: 20px;
}

.content {
  padding: 12px;
}

.assessment-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 14px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: transform 0.2s;
}

.card:active {
  transform: scale(0.98);
}

.card-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #f7f8fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  font-size: 13px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 4px;
}

.card-content p {
  font-size: 11px;
  color: #969799;
  margin-bottom: 6px;
}

.arrow {
  flex-shrink: 0;
  color: #c8c9cc;
}

.history-section {
  margin-top: 20px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #323233;
  padding: 8px 4px;
}

.history-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.history-item {
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f7f8fa;
  cursor: pointer;
}

.history-item:last-child {
  border-bottom: none;
}

.history-item:active {
  background: #f7f8fa;
}

.history-info {
  flex: 1;
}

.history-name {
  font-size: 12px;
  color: #323233;
  margin-bottom: 4px;
}

.history-date {
  font-size: 10px;
  color: #969799;
}

.history-score {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.score-value {
  font-size: 18px;
  font-weight: bold;
}

.score-value.excellent {
  color: #52c41a;
}

.score-value.good {
  color: #faad14;
}

.score-value.poor {
  color: #ff6b6b;
}

.score-label {
  font-size: 10px;
  color: #969799;
}
</style>

