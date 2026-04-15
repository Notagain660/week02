<template>
  <el-card class="chat-list">
    <div v-if="chatUsers.length === 0" class="empty">暂无聊天记录</div>
    <div
        v-for="user in chatUsers"
        :key="user.userId"
        class="chat-item"
        @click="openChat(user.userId)"
    >
      <el-avatar :size="40" :src="user.userAvatar" />
      <div class="info">
        <div class="name">{{ user.userName }}</div>
        <div class="last-msg">{{ user.lastMsg || '暂无消息' }}</div>
      </div>
      <div v-if="user.unreadCount > 0" class="unread-badge">{{ user.unreadCount }}</div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRelationsByStatus } from '@/api/relation'
import { getOtherProfile } from '@/api/user'
import { useUserStore } from '@/stores/user'        // ✅ 导入移到顶部

const router = useRouter()
const userStore = useUserStore()                   // ✅ 初始化移到顶部
const chatUsers = ref([])

const fetchChats = async () => {
  const res = await getRelationsByStatus(2)
  if (res.code === 200) {
    const friends = res.data
    const users = []
    for (const f of friends) {
      const friendId = f.myId === userStore.userInfo?.userId ? f.itsId : f.myId
      const profile = await getOtherProfile(friendId)
      if (profile.code === 200) {
        users.push({
          userId: friendId,
          userName: profile.data.nickname,
          userAvatar: profile.data.avatar,
          lastMsg: '暂无消息',
          unreadCount: 0,
        })
      }
    }
    chatUsers.value = users
  }
}

const openChat = (userId) => {
  router.push(`/chat/${userId}`)
}

onMounted(fetchChats)
</script>

<style scoped>
/* 样式保持不变 */
</style>