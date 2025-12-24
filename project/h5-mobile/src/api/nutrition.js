import { http } from '@/utils/http'

// 生成营养报告
export function generateReport(data) {
  return http.post('/api/nutrition/report/generate', data)
}

// 获取报告详情
export function getReportDetail(id) {
  return http.get(`/api/nutrition/report/${id}`)
}

// 查询报告列表
export function getReportList(data) {
  return http.post('/api/nutrition/report/list', data)
}

// 删除报告
export function deleteReport(id) {
  return http.delete(`/api/nutrition/report/${id}`)
}

// 获取最新报告
export function getLatestReport(reportType) {
  return http.get('/api/nutrition/report/latest', { params: { reportType } })
}

// 获取营养趋势
export function getNutritionTrend(startDate, endDate) {
  return http.get('/api/nutrition/report/trend', { params: { startDate, endDate } })
}

// 重新生成AI建议
export function regenerateAIAdvice(id) {
  return http.post(`/api/nutrition/report/regenerate/${id}`)
}
