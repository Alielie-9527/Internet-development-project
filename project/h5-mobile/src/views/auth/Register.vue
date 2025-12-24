<template>
  <div class="register-page">
    <van-nav-bar title="注册账号" left-arrow @click-left="$router.back()" fixed />

    <div class="content">
      <div class="register-form">
        <van-form @submit="onSubmit">
          <van-cell-group inset>
            <van-field
              v-model="formData.username"
              name="username"
              label="用户名"
              placeholder="请输入用户名"
              :rules="[
                { required: true, message: '请输入用户名' },
                { pattern: /^[a-zA-Z0-9_]{4,16}$/, message: '用户名4-16位字母数字下划线' }
              ]"
            />
            <van-field
              v-model="formData.password"
              type="password"
              name="password"
              label="密码"
              placeholder="请输入密码"
              :rules="[
                { required: true, message: '请输入密码' },
                { minLength: 6, message: '密码至少6位' }
              ]"
            />
            <van-field
              v-model="formData.confirmPassword"
              type="password"
              name="confirmPassword"
              label="确认密码"
              placeholder="请再次输入密码"
              :rules="[{ validator: validatePassword }]"
            />
            <van-field
              v-model="formData.nickname"
              name="nickname"
              label="昵称"
              placeholder="请输入昵称"
              :rules="[{ required: true, message: '请输入昵称' }]"
            />
            <van-field
              v-model="formData.email"
              name="email"
              type="email"
              label="邮箱"
              placeholder="请输入邮箱（选填）"
            />
          </van-cell-group>

          <div class="btn-group">
            <van-button round block type="primary" native-type="submit" :loading="loading">
              注册
            </van-button>
            <van-button round block plain type="primary" @click="goToLogin">
              已有账号？去登录
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register } from '@/api/user'

const router = useRouter()

const loading = ref(false)
const formData = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

function validatePassword(val) {
  if (!val) {
    return '请输入确认密码'
  }
  if (val !== formData.value.password) {
    return '两次密码输入不一致'
  }
  return true
}

async function onSubmit() {
  try {
    loading.value = true
    console.log('开始注册:', formData.value.username)
    
    await register({
      username: formData.value.username,
      password: formData.value.password,
      nickname: formData.value.nickname,
      email: formData.value.email || null
    })
    
    console.log('注册成功')
    showToast('注册成功，请登录')
    
    // 延迟跳转
    setTimeout(() => {
      router.replace('/login')
    }, 1500)
    
  } catch (error) {
    console.error('注册失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '注册失败，请重试'
    showToast(errorMsg)
  } finally {
    loading.value = false
  }
}

function goToLogin() {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
}

.content {
  padding: 20px;
}

.register-form {
  background: white;
  border-radius: 16px;
  padding: 28px 20px;
}

.btn-group {
  padding: 24px 0 10px;
}

.btn-group .van-button {
  margin-bottom: 16px;
  height: 48px !important;
  font-size: 16px !important;
}
</style>
