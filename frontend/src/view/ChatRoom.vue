<template>
  <div>
    <h2>与用户 {{ otherUserId }} 的聊天记录</h2>
    <MarkReadButton/>
    <DeleteChatButton/>
    <div v-if="loading">加载中...</div>
    <div v-else-if="messages.length === 0">暂无消息</div>
    <ul v-else>
      <li v-for="msg in messages" :key="msg.batchchat"  class="message-item">
        <div style="font-size: 10px; color: gray;">senderId={{ msg.senderId }}, currentUserId={{ store.currentUser?.userId }}, chatId={{msg.batchchat}}</div>
        <strong>{{ Number(msg.senderId) === myId ? '我' : '对方' }}</strong>：{{ msg.chatContent }}
        <span style="margin-left: 10px; font-size: 12px;">{{ formatTime(msg.sendTime) }}</span>
        <span :style="{ color: msg.chatStatus === 'READ' ? 'green' : 'red' }">
          {{ msg.chatStatus === 'READ' ? '已读' : '未读' }}
        </span>
      </li>
    </ul>
    <SendMessageButton :receiver-id="otherUserId" @success="fetchMessages" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getChatsWithUser } from '@/api/user'
import { useStore } from '@/store'
import MarkReadButton from "@/components/MarkReadButton.vue"
import DeleteChatButton from "@/components/DeleteChatButton.vue"
import SendMessageButton from "@/components/SendMessageButton.vue";


const route = useRoute()
const store = useStore()
const otherUserId = Number(route.params.userId)
const messages = ref([])
const loading = ref(false)
let myId = null

const fetchMessages = async () => {
  if (!otherUserId) return
  loading.value = true
  try {

    if (!store.currentUser) {
      await store.fetchUserInfo()
    }
    myId = Number(store.currentUser?.userId)
    if (!myId) {
      console.error('无法获取当前用户ID')
      return
    }

    const res = await getChatsWithUser(otherUserId)
    if (res.code === 200) {
      messages.value = res.data
      console.log(store.currentUser?.userId)
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => new Date(time).toLocaleString()

onMounted(fetchMessages)
</script>