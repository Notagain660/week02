<template>
  <div>
    <input v-model="userId" placeholder="输入对方用户ID" />
    <button @click="addFriend" :disabled="!userId">添加好友</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { sendFriendRequest } from '@/api/user'

const userId = ref('')

const addFriend = async () => {
  if (!userId.value) return
  try {
    const res = await sendFriendRequest(userId.value)
    if (res.code === 200) {
      ElMessage.success('好友请求已发送')
      userId.value = ''
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
}
</script>