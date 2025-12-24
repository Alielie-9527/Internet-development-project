<template>
  <div class="login-page">
    <div class="header">
      <h1>智能营养助手</h1>
      <p>您的健康饮食管家</p>
    </div>

    <div class="login-form">
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="formData.username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
          />
          <van-field
            v-model="formData.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </van-cell-group>

        <div class="btn-group">
          <van-button round block type="primary" native-type="submit" :loading="loading">
            登录
          </van-button>
          <van-button round block @click="goToRegister">
            注册账号
          </van-button>
        </div>
      </van-form>
      <div class="hint">测试账号：testuser / admin123</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const formData = ref({
  username: '',
  password: ''
})

async function onSubmit() {
  if (!formData.value.username || !formData.value.password) {
    showToast('请输入用户名和密码')
    return
  }

  try {
    loading.value = true
    console.log('开始登录:', formData.value.username)
    
    // 调用登录API
    const data = await login(formData.value)
    console.log('登录响应数据:', data)
    
    if (!data || !data.token) {
      showToast('登录失败：未获取到token')
      return
    }
    
    // 保存用户信息和token
    userStore.login(data)
    
    showToast('登录成功')
    
    // 延迟跳转，确保状态已保存
    setTimeout(() => {
      const redirect = route.query.redirect || '/home'
      router.replace(redirect)
    }, 500)
    
  } catch (error) {
    console.error('登录失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '登录失败，请重试'
    showToast(errorMsg)
  } finally {
    loading.value = false
  }
}

function goToRegister() {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.header {
  text-align: center;
  color: white;
  margin-bottom: 32px;
  padding-top: 16px;
}

.header h1 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 12px;
}

.header p {
  font-size: 16px;
  opacity: 0.9;
}

.login-form {
  background: white;
  border-radius: 16px;
  padding: 32px 24px;
  max-width: 420px;
  margin: 0 auto;
}

.btn-group {
  padding: 24px 0;
}

.btn-group .van-button {
  margin-bottom: 16px;
  height: 48px !important;
  font-size: 16px !important;
}

.hint {
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-top: 12px;
}
</style>

