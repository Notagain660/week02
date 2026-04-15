<template>
  <div class="chat-room">
    <div class="header">
      <el-button text @click="router.back()">返回</el-button>
      <span>与 {{ otherName }} 聊天中</span>
    </div>
    <div class="messages" ref="msgContainer">
      <div
          v-for="msg in messages"
          :key="msg.batchchat"
          class="message"
          :class="{ self: msg.senderId === myId }"
      >
        <div class="content">{{ msg.chatContent }}</div>
        <div class="time">{{ formatTime(msg.sendTime) }}</div>
        <div v-if="msg.senderId !== myId && msg.chatStatus === 0" class="unread">未读</div>
      </div>
    </div>
    <div class="input-area">
      <el-input
          v-model="input"
          type="textarea"
          :rows="2"
          placeholder="输入消息"
          @keyup.ctrl.enter="send"
      />
      <el-button type="primary" @click="send">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getChatsWithUser, sendChat, markChatRead } from '@/api/chat'
import { getOtherProfile } from '@/api/user'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()          // ✅ 显式引入 router
const userStore = useUserStore()
const myId = userStore.userInfo?.userId
const otherId = Number(route.params.userId)
const messages = ref([])
const input = ref('')
const otherName = ref('')
const msgContainer = ref(null)
let timer = null

const fetchMessages = async () => {
  if (!myId) return                    // ✅ 防止 myId 为 undefined
  const res = await getChatsWithUser(otherId)
  if (res.code === 200) {
    messages.value = res.data
    await nextTick()
    if (msgContainer.value) {
      msgContainer.value.scrollTop = msgContainer.value.scrollHeight
    }
    // 标记对方发来的未读消息为已读
    for (const msg of messages.value) {
      if (msg.receiverId === myId && msg.chatStatus === 0) {
        await markChatRead(msg.batchchat)
      }
    }
  }
}

const send = async () => {
  if (!input.value.trim()) return
  await sendChat(otherId, input.value)
  input.value = ''
  await fetchMessages()
}

const formatTime = (t) => dayjs(t).format('HH:mm')

const startPolling = () => {
  timer = setInterval(fetchMessages, 3000)
}

onMounted(async () => {
  const res = await getOtherProfile(otherId)
  if (res.code === 200) otherName.value = res.data.nickname
  await fetchMessages()
  startPolling()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 80vh;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}
.header {
  padding: 12px 16px;
  background-color: white;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 16px;
}
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.message {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.message.self {
  align-items: flex-end;
}
.content {
  background-color: #fff;
  padding: 8px 12px;
  border-radius: 12px;
  max-width: 70%;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
.message.self .content {
  background-color: #409eff;
  color: white;
}
.time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.unread {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 2px;
}
.input-area {
  display: flex;
  gap: 12px;
  padding: 12px;
  background-color: white;
  border-top: 1px solid #eee;
}
.input-area .el-textarea {
  flex: 1;
}
</style>