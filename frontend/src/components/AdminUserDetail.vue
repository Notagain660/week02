<template>
  <div>
    <h2>用户信息（管理员查看）</h2>
    <div v-if="loading">加载中...</div>
    <div v-else-if="user">
      <img :src="user.userAvatar" alt="头像" width="100" />
      <div>昵称：{{ user.userName }}</div>
      <div>ID：{{ user.userId }}</div>
      <div>状态：{{ user.status === 0 ? '正常' : '封禁' }}</div>
      <div>发帖数：{{ user.userPostNum }}</div>
      <div>手机号：{{ user.userPhone }}</div>
      <div>邮箱：{{ user.userEmail }}</div>
      <div>角色：{{ user.role === 'USER' ? '学生' : '管理员' }}</div>
    </div>
    <div v-else>用户不存在</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { adminCheckUser } from '@/api/user'
import { useStore } from '@/store'

const route = useRoute()
const store = useStore()
const userId = route.params.userId
const user = ref(null)
const loading = ref(false)

// 非管理员访问时显示空或提示（可选，最好在路由守卫中拦截）
// 这里简单判断，如果不是管理员则清空数据
const isAdmin = store.currentUser?.role === 'ADMIN'

const fetchUser = async () => {
  if (!isAdmin || !userId) return
  loading.value = true
  try {
    const res = await adminCheckUser(userId)
    if (res.code === 200) {
      user.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchUser)
</script>