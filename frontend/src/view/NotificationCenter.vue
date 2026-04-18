<template>
  <div class="notification-container">
    <h2>消息中心</h2>
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="评论回复" name="comments">
        <div v-if="commentLoading">加载中...</div>
        <div v-else-if="comments.length === 0">暂无回复</div>
        <div v-else>
          <div v-for="item in comments" :key="item.batchco" class="notify-item">
            <div class="content">{{ item.commentText }}</div>
            <div class="meta">来自用户 {{ item.commenterId }} · {{ formatTime(item.replyTime) }}</div>
            <button @click="goToPost(item.postId)">查看帖子</button>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="私聊消息" name="chats">
        <div v-if="chatLoading">加载中...</div>
        <div v-else-if="chats.length === 0">暂无消息</div>
        <div v-else>
          <div v-for="item in chats" :key="item.batchchat" class="notify-item">
            <div class="content">{{ item.chatContent }}</div>
            <div class="meta">来自用户 {{ item.senderId }} · {{ formatTime(item.sendTime) }}</div>
            <button @click="goToChat(item.senderId)">回复</button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRepliedComments, getReceivedChats } from '@/api/user.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeTab = ref('comments')
const comments = ref([])
const chats = ref([])
const commentLoading = ref(false)
const chatLoading = ref(false)

const fetchComments = async () => {
  commentLoading.value = true
  try {
    const res = await getRepliedComments()
    if (res.code === 200) {
      comments.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  } finally {
    commentLoading.value = false
  }
}

const fetchChats = async () => {
  chatLoading.value = true
  try {
    const res = await getReceivedChats()
    if (res.code === 200) {
      chats.value = res.data.filter(item => item.chatStatus === 0)
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  } finally {
    chatLoading.value = false
  }
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'comments' && comments.value.length === 0) {
    fetchComments()
  } else if (tab.paneName === 'chats' && chats.value.length === 0) {
    fetchChats()
  }
}

const goToPost = (postId) => {
  router.push(`/post/${postId}`)
}

const goToChat = (senderId) => {
  router.push(`/chat/${senderId}`)
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''

onMounted(() => {
  // 默认加载评论回复
  fetchComments()
})
</script>

<style scoped>
.notification-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.notify-item {
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}
.content {
  margin-bottom: 6px;
}
.meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}
button {
  padding: 4px 12px;
  cursor: pointer;
}
</style>