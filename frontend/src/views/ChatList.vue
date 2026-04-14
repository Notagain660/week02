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

const router = useRouter()
const chatUsers = ref([])

const fetchChats = async () => {
  const res = await getRelationsByStatus(2)  // 获取好友列表
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
// 需要引入 userStore
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
onMounted(fetchChats)
</script>


<style scoped>
.chat-list {
  max-width: 600px;
  margin: 0 auto;
}
.chat-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}
.chat-item:hover {
  background-color: #f5f7fa;
}
.info {
  flex: 1;
  margin-left: 12px;
}
.name {
  font-weight: bold;
}
.last-msg {
  font-size: 12px;
  color: #999;
}
.unread-badge {
  background-color: #f56c6c;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
  line-height: 20px;
}
.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>