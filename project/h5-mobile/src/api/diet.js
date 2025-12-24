import { http } from '@/utils/http'

// 添加饮食日记
export function addDietDiary(data) {
  return http.post('/api/diet/diary/add', data)
}

// 更新饮食日记
export function updateDietDiary(data) {
  return http.put('/api/diet/diary/update', data)
}

// 删除饮食日记
export function deleteDietDiary(id) {
  return http.delete(`/api/diet/diary/delete/${id}`)
}

// 获取饮食日记详情
export function getDietDiaryDetail(id) {
  return http.get(`/api/diet/diary/detail/${id}`)
}

// 查询饮食日记列表
export function getDietDiaryList(data) {
  return http.post('/api/diet/diary/list', data)
}

// 查询某日期的饮食记录
export function getDietDiaryByDate(date) {
  return http.get(`/api/diet/diary/date/${date}`)
}

// 查询日期范围的饮食记录
export function getDietDiaryByDateRange(startDate, endDate) {
  return http.get('/api/diet/diary/dateRange', { params: { startDate, endDate } })
}

// 获取某日期的饮食统计
export function getDietStatistics(date) {
  return http.get(`/api/diet/diary/statistics/${date}`)
}
