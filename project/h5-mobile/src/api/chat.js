import { http } from '@/utils/http'

// 创建会话
export function createSession(data) {
  return http.post('/api/chat/sessions', data)
}

// 会话列表
export function listSessions() {
  return http.get('/api/chat/sessions')
}

// 会话详情
export function getSession(sessionId) {
  return http.get(`/api/chat/sessions/${sessionId}`)
}

// 消息历史
export function getMessages(sessionId) {
  return http.get('/api/chat/messages', { params: { sessionId } })
}

// 发送消息（返回AI回复）
export function sendMessage(payload) {
  return http.post('/api/chat/messages', payload)
}

// 删除会话
export function deleteSession(sessionId) {
  return http.delete(`/api/chat/sessions/${sessionId}`)
}