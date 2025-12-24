import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // State - 立即从localStorage加载
  const token = ref(localStorage.getItem('token') || '')
  const storedUser = localStorage.getItem('userInfo')
  let initialUserInfo = null
  if (storedUser) {
    try {
      initialUserInfo = JSON.parse(storedUser)
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  const userInfo = ref(initialUserInfo)

  // Getters
  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const nickname = computed(() => userInfo.value?.nickname)

  // Actions
  function setToken(newToken) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  function setUserInfo(info) {
    userInfo.value = info
  }

  function login(loginData) {
    // 登录逻辑在API调用中处理
    setToken(loginData.token)
    setUserInfo(loginData)
    // 同时保存到localStorage
    localStorage.setItem('userInfo', JSON.stringify(loginData))
  }

  function logout() {
    setToken('')
    setUserInfo(null)
    localStorage.clear()
  }

  function initUser() {
    // 重新从localStorage加载用户信息
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('userInfo')
    
    if (storedToken) {
      token.value = storedToken
    }
    
    if (storedUser) {
      try {
        userInfo.value = JSON.parse(storedUser)
      } catch (e) {
        console.error('解析用户信息失败:', e)
        // 如果解析失败，清除无效数据
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
        token.value = ''
        userInfo.value = null
      }
    }
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    userId,
    username,
    nickname,
    setToken,
    setUserInfo,
    login,
    logout,
    initUser
  }
})

