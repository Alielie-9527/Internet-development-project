import { http } from '@/utils/http'

// 分析食物图片（文件上传）
export function analyzeFoodImageUpload(file, userId) {
  const formData = new FormData()
  formData.append('file', file)
  if (userId) {
    formData.append('userId', userId)
  }
  
  return http.post('/api/ai/analyze-food/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000 // 60秒超时
  })
}

// 分析食物图片（Base64）
export function analyzeFoodImage(data) {
  return http.post('/api/ai/analyze-food', data, {
    timeout: 60000
  })
}

// 搜索食物
export function searchFood(keyword, page = 1, size = 20) {
  return http.post('/api/food/search', {
    keyword: keyword,
    page: page,
    size: size
  })
}

// 获取食物详情
export function getFoodDetail(id) {
  return http.get(`/api/food/${id}`)
}
