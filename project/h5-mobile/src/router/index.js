import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/HomePage.vue'),
    meta: { title: '首页', requiresAuth: true, keepAlive: true }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/views/chat/ChatView.vue'),
    meta: { title: 'AI对话', requiresAuth: true }
  },
  // 饮食日记相关路由
  {
    path: '/diet/list',
    name: 'DietList',
    component: () => import('@/views/diet/DietList.vue'),
    meta: { title: '饮食日记', requiresAuth: true, keepAlive: true }
  },
  {
    path: '/diet/add',
    name: 'DietAdd',
    component: () => import('@/views/diet/DietAdd.vue'),
    meta: { title: '添加饮食记录', requiresAuth: true }
  },
  {
    path: '/diet/edit/:id',
    name: 'DietEdit',
    component: () => import('@/views/diet/DietAdd.vue'),
    meta: { title: '编辑饮食记录', requiresAuth: true }
  },
  {
    path: '/diet/detail/:id',
    name: 'DietDetail',
    component: () => import('@/views/diet/DietDetail.vue'),
    meta: { title: '饮食详情', requiresAuth: true }
  },
  // 体重管理相关路由
  {
    path: '/weight/list',
    name: 'WeightList',
    component: () => import('@/views/weight/WeightList.vue'),
    meta: { title: '体重管理', requiresAuth: true, keepAlive: true }
  },
  // 营养报告相关路由
  {
    path: '/report/list',
    name: 'ReportList',
    component: () => import('@/views/report/ReportList.vue'),
    meta: { title: '营养报告', requiresAuth: true, keepAlive: true }
  },
  {
    path: '/report/detail/:id',
    name: 'ReportDetail',
    component: () => import('@/views/report/ReportDetail.vue'),
    meta: { title: '报告详情', requiresAuth: true }
  },
  // 健康评估路由
  {
    path: '/assessment/list',
    name: 'AssessmentList',
    component: () => import('@/views/assessment/AssessmentList.vue'),
    meta: { title: '健康评估', requiresAuth: true }
  },
  // 目标设置路由
  {
    path: '/user/goal-settings',
    name: 'GoalSettings',
    component: () => import('@/views/user/GoalSettings.vue'),
    meta: { title: '目标设置', requiresAuth: true }
  },
  // 食物分析路由
  {
    path: '/food/analysis',
    name: 'FoodAnalysis',
    component: () => import('@/views/food-analysis/FoodAnalysis.vue'),
    meta: { title: 'AI识别食物', requiresAuth: true }
  },
  // 营养知识路由
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('@/views/knowledge/KnowledgeList.vue'),
    meta: { title: '营养知识', requiresAuth: true }
  },
  // 用户中心
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/UserCenter.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  // 登录
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  // 注册
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '营养健康管理'

  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  const hasUserInfo = !!localStorage.getItem('userInfo')
  const isAuthenticated = token && hasUserInfo
  
  console.log('Route Guard:', {
    to: to.name,
    hasToken: !!token,
    hasUserInfo,
    isAuthenticated,
    requiresAuth: to.meta.requiresAuth
  })
  
  // 如果已登录且访问登录页，重定向到首页
  if ((to.name === 'Login' || to.name === 'Register') && isAuthenticated) {
    next({ name: 'Home' })
    return
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    if (!isAuthenticated) {
      console.log('未登录，跳转到登录页')
      showToast('请先登录')
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }

  next()
})

export default router

