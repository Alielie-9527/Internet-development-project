import { http } from '@/utils/http'

// 添加体重记录
export function addWeightRecord(data) {
  return http.post('/api/weight/add', data)
}

// 更新体重记录
export function updateWeightRecord(data) {
  return http.put('/api/weight/update', data)
}

// 删除体重记录
export function deleteWeightRecord(id) {
  return http.delete(`/api/weight/delete/${id}`)
}

// 获取体重记录详情
export function getWeightRecordDetail(id) {
  return http.get(`/api/weight/detail/${id}`)
}

// 查询体重记录列表
export function getWeightRecordList(data) {
  return http.post('/api/weight/list', data)
}

// 获取最新体重记录
export function getLatestWeightRecord() {
  return http.get('/api/weight/latest')
}

// 获取体重趋势图表数据
export function getWeightTrend(startDate, endDate) {
  return http.get('/api/weight/trend', { params: { startDate, endDate } })
}

// 获取体重统计
export function getWeightStatistics() {
  return http.get('/api/weight/statistics')
}
