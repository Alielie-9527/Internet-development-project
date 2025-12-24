<template>
  <div class="chat-page">
    <van-nav-bar title="饮食健康咨询" left-arrow @click-left="onClickLeft">
      <template #right>
        <van-icon name="wap-nav" @click="showSessionList" style="margin-right: 12px;" />
        <van-icon name="plus" @click="startNewChat" style="margin-right: 12px;" />
        <van-icon name="service-o" @click="checkHealth" />
      </template>
    </van-nav-bar>

    <div class="chat-container" ref="chatContainer">
      <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role]">
        <div class="message-content">{{ msg.content }}</div>
        <div v-if="msg.role === 'assistant' && msg.createdAt" class="message-time">
          {{ formatTime(msg.createdAt) }}
        </div>
      </div>
    </div>

    <div class="input-bar">
      <van-field
        v-model="inputText"
        placeholder="说点什么..."
        @keyup.enter="sendMessage"
      />
      <van-button type="primary" @click="sendMessage" :loading="sending">
        发送
      </van-button>
    </div>

    <!-- 会话列表弹窗 -->
    <van-popup v-model:show="sessionListVisible" position="right" :style="{ width: '80%', height: '100%' }">
      <div class="session-list-container">
        <div class="session-list-header">
          <span>历史会话</span>
          <van-icon name="cross" @click="sessionListVisible = false" />
        </div>
        <div class="session-list-content">
          <div
            v-for="session in sessionList"
            :key="session.id"
            :class="['session-item', { active: session.id === sessionId }]"
          >
            <div class="session-info" @click="switchSession(session)">
              <div class="session-title">{{ session.title || '未命名会话' }}</div>
              <div class="session-time">{{ formatTime(session.createdAt) }}</div>
            </div>
            <van-icon 
              name="delete-o" 
              class="delete-icon" 
              @click.stop="confirmDelete(session)"
            />
          </div>
          <div v-if="!sessionList.length" class="empty-tip">暂无历史会话</div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { createSession, listSessions, getMessages, sendMessage as sendMsgApi, deleteSession } from '@/api/chat'
import { health as aiHealth, testChat } from '@/api/ai'

const router = useRouter()
const chatContainer = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const sessionId = ref(null)
const sessionList = ref([])
const sessionListVisible = ref(false)

function onClickLeft() {
  router.back()
}

onMounted(async () => {
  // 初始化会话：优先使用现有会话，否则创建新会话
  try {
    // 先尝试获取最近的会话
    await loadSessionList()
    
    if (sessionList.value && sessionList.value.length > 0) {
      // 使用最近的会话
      sessionId.value = sessionList.value[0].id
      console.log('使用现有会话:', sessionId.value)
    } else {
      // 没有会话，创建新会话
      const data = await createSession({ title: `饮食健康咨询_${new Date().toLocaleDateString()}` })
      sessionId.value = data.id
      console.log('创建新会话:', sessionId.value)
      await loadSessionList() // 重新加载会话列表
    }
    
    // 加载历史消息
    await loadMessages()
    
  } catch (e) {
    console.error('初始化会话失败:', e)
    // 创建新会话作为降级方案
    try {
      const data = await createSession({ title: `饮食健康咨询_${new Date().toLocaleDateString()}` })
      sessionId.value = data.id
      messages.value = [{
        id: 'welcome',
        role: 'assistant',
        content: '你好！我是智能营养师AI助手，专注于饮食健康咨询。我可以帮您分析食物营养、制定饮食计划、提供健康建议。请问有什么可以帮助您的吗？'
      }]
      await nextTick()
      scrollToBottom()
    } catch (err) {
      console.error('降级创建会话也失败:', err)
    }
  }
})

async function sendMessage() {
  if (!inputText.value.trim()) return

  const userMessage = {
    id: Date.now(),
    role: 'user',
    content: inputText.value
  }

  messages.value.push(userMessage)
  inputText.value = ''

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  sending.value = true
  try {
    if (sessionId.value) {
      // 调用聊天消息接口（返回AI回复）
      const res = await sendMsgApi({ sessionId: sessionId.value, content: userMessage.content })
      const replyContent = res?.content || '我理解你的感受。我们可以一起梳理下。'
      messages.value.push({ id: Date.now() + 1, role: 'assistant', content: replyContent })
    } else {
      // 退化到AI单轮对话
      const data = await testChat(userMessage.content)
      const replyContent = data?.content || '我理解你的感受。我们可以一起梳理下。'
      messages.value.push({ id: Date.now() + 1, role: 'assistant', content: replyContent })
    }
  } catch (e) {
    console.error(e)
    showToast('发送失败，请稍后再试')
  } finally {
    sending.value = false
    nextTick(() => scrollToBottom())
  }
}

function scrollToBottom() {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

async function checkHealth() {
  try {
    await aiHealth()
    showToast('AI服务正常')
  } catch (e) {
    showToast('AI服务异常')
  }
}

// 加载会话列表
async function loadSessionList() {
  try {
    const list = await listSessions()
    sessionList.value = list || []
  } catch (e) {
    console.error('加载会话列表失败:', e)
    sessionList.value = []
  }
}

// 加载消息
async function loadMessages() {
  if (!sessionId.value) return
  
  try {
    const list = await getMessages(sessionId.value)
    messages.value = (list || []).map((m) => ({
      id: m.id || `${m.role}_${m.timestamp || Math.random()}`,
      role: String(m.role || '').toLowerCase() === 'assistant' ? 'assistant' : 'user',
      content: m.content || '',
      createdAt: m.createdAt
    }))
    
    // 只有在完全没有消息时才显示欢迎语
    if (messages.value.length === 0) {
      messages.value.push({
        id: 'welcome',
        role: 'assistant',
        content: '你好！我是智能营养师AI助手，专注于饮食健康咨询。我可以帮您分析食物营养、制定饮食计划、提供健康建议。请问有什么可以帮助您的吗？'
      })
    }
    
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('加载消息失败:', e)
  }
}

// 显示会话列表
function showSessionList() {
  sessionListVisible.value = true
}

// 切换会话
async function switchSession(session) {
  if (session.id === sessionId.value) {
    sessionListVisible.value = false
    return
  }
  
  sessionId.value = session.id
  sessionListVisible.value = false
  await loadMessages()
  showToast('已切换会话')
}

// 确认删除会话
async function confirmDelete(session) {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除"${session.title || '未命名会话'}"吗？`,
      confirmButtonText: '删除',
      confirmButtonColor: '#ee0a24'
    })
    await handleDelete(session)
  } catch {
    // 用户取消删除
  }
}

// 删除会话
async function handleDelete(session) {
  try {
    await deleteSession(session.id)
    
    // 如果删除的是当前会话，需要切换到其他会话或创建新会话
    if (session.id === sessionId.value) {
      await loadSessionList()
      
      if (sessionList.value.length > 0) {
        // 切换到第一个会话
        sessionId.value = sessionList.value[0].id
        await loadMessages()
      } else {
        // 没有会话了，创建新会话
        const data = await createSession({ title: `饮食健康咨询_${new Date().toLocaleDateString()}` })
        sessionId.value = data.id
        messages.value = [{
          id: 'welcome',
          role: 'assistant',
          content: '你好！我是智能营养师AI助手，专注于饮食健康咨询。我可以帮您分析食物营养、制定饮食计划、提供健康建议。请问有什么可以帮助您的吗？'
        }]
        await loadSessionList()
        await nextTick()
        scrollToBottom()
      }
    } else {
      // 删除的不是当前会话，只需要刷新列表
      await loadSessionList()
    }
    
    showToast('删除成功')
  } catch (e) {
    console.error('删除会话失败:', e)
    showToast('删除失败，请稍后重试')
  }
}

// 开始新对话
async function startNewChat() {
  try {
    const data = await createSession({ title: `饮食健康咨询_${new Date().toLocaleDateString()}` })
    sessionId.value = data.id
    messages.value = [{
      id: 'welcome',
      role: 'assistant',
      content: '你好！我是智能营养师AI助手，专注于饮食健康咨询。我可以帮您分析食物营养、制定饮食计划、提供健康建议。请问有什么可以帮助您的吗？'
    }]
    await loadSessionList() // 重新加载会话列表
    await nextTick()
    scrollToBottom()
    showToast('已创建新对话')
  } catch (e) {
    console.error('创建新对话失败:', e)
    showToast('创建失败，请稍后重试')
  }
}

// 格式化时间
function formatTime(datetime) {
  if (!datetime) return ''
  const date = new Date(datetime)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return `${date.getMonth() + 1}/${date.getDate()}`
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f7f8fa;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message {
  margin-bottom: 10px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 8px 10px;
  border-radius: 8px;
  word-wrap: break-word;
  font-size: 12px;
  line-height: 1.4;
}

.message.user .message-content {
  background-color: #4a90e2;
  color: white;
}

.message.assistant .message-content {
  background-color: white;
  color: #333;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.message-time {
  font-size: 10px;
  color: #999;
  margin-top: 4px;
  text-align: left;
}

.input-bar {
  display: flex;
  align-items: center;
  padding: 12px;
  background-color: white;
  border-top: 1px solid #eee;
}

.input-bar :deep(.van-field) {
  flex: 1;
  margin-right: 12px;
}

.session-list-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f5f5;
}

.session-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #eee;
  font-weight: bold;
}

.session-list-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  background: white;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.session-item.active {
  background: #e8f4ff;
  border-left: 3px solid #1989fa;
}

.session-item:active {
  transform: scale(0.98);
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-time {
  font-size: 12px;
  color: #999;
}

.delete-icon {
  color: #ee0a24;
  font-size: 18px;
  padding: 8px;
  margin-left: 8px;
  flex-shrink: 0;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}
</style>

