<template>
  <button @click="addFriend" :disabled="loading">添加好友</button>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { sendFriendRequest } from '@/api/user'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const loading = ref(false)

const addFriend = async () => {
  loading.value = true
  try {
    const res = await sendFriendRequest(props.userId)
    if (res.code === 200) {
      ElMessage.success('好友请求已发送')
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  } finally {
    loading.value = false
  }
}
</script>