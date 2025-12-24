<template>
  <div class="user-page">
    <van-nav-bar title="个人中心" />

    <div class="user-header">
      <van-image
        round
        width="56"
        height="56"
        :src="userInfo?.avatar || defaultAvatar"
      />
      <div class="user-info">
        <h3>{{ userInfo?.nickname || '未登录' }}</h3>
        <p>@{{ userInfo?.username }}</p>
      </div>
    </div>

    <van-cell-group>
      <van-cell title="饮食记录" is-link icon="records" @click="goToDiet" />
      <van-cell title="体重记录" is-link icon="balance-o" @click="goToWeight" />
      <van-cell title="营养报告" is-link icon="bar-chart-o" @click="goToReport" />
      <van-cell title="个人信息" is-link icon="contact" @click="goToProfile" />
      <van-cell title="设置" is-link icon="setting-o" @click="goToSettings" />
    </van-cell-group>

    <div class="logout-btn">
      <van-button type="danger" block @click="handleLogout">退出登录</van-button>
    </div>

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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const active = ref(3)

const userInfo = computed(() => userStore.userInfo)
const defaultAvatar = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

function goToDiet() {
  router.push('/diet/list')
}

function goToWeight() {
  router.push('/weight/list')
}

function goToReport() {
  router.push('/report/list')
}

function goToProfile() {
  showToast('功能开发中...')
}

function goToSettings() {
  showToast('功能开发中...')
}

async function handleLogout() {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })
    userStore.logout()
    showToast('已退出登录')
    router.push('/login')
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 60px;
}

.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px 12px;
  display: flex;
  align-items: center;
  color: white;
}

.user-info {
  margin-left: 12px;
}

.user-info h3 {
  font-size: 16px;
  margin-bottom: 4px;
}

.user-info p {
  font-size: 12px;
  opacity: 0.9;
}

.logout-btn {
  margin: 20px 16px;
}
</style>

