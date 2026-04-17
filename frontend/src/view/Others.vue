<template>
  <div>
    <h2>他人信息</h2>
    <div v-if="loading">加载中...</div>
    <div v-else-if="user">
      <img :src="user.avatar" alt="头像" style="width: 100px; height: 100px;" />
      <div>昵称：{{ user.nickname }}</div>
      <div>ID：{{ userId }}</div>
      <div>状态：{{ user.status === 0 ? '正常' : '封禁' }}</div>
      <div>发帖数：{{ user.postNum }}</div>
      <OtherproAdd :userId="Number(userId)" />
      <UserPosts :user-id="userId" />
    </div>
    <div v-else>用户不存在</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOtherProfile } from '@/api/user'
import OtherproAdd from '@/components/OtherproAdd.vue'
import UserPosts from "@/components/UserPosts.vue";

const route = useRoute()
const userId = route.params.userId
const user = ref(null)
const loading = ref(false)

onMounted(async () => {
  if (!userId) return
  loading.value = true
  try {
    const res = await getOtherProfile(userId)
    if (res.code === 200) {
      user.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
})
</script>