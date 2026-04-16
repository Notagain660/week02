<script setup>
import {onMounted, ref} from 'vue'
import { useStore } from '@/store'
import AvatarUpdater from '@/components/AvatarUpdater.vue'
import NameUpdater from "@/components/NameUpdater.vue";

const store = useStore()
const currentUser = ref(null)

onMounted(async () => {
  try {
    await store.fetchUserInfo()   // 更新 store 中的 currentUser
    currentUser.value = store.currentUser  // 从 store 中读取
    if (!currentUser.value) {
      alert('获取个人信息失败')
    }
  } catch (error) {
    console.error(error)
    alert('请求出错')
  }
})
</script>

<template>
  <div>
    <h1>查看我的信息</h1>
    <div>
      <AvatarUpdater/>
    </div>
    <div>
      <NameUpdater/>
    </div>
    <div>
      <img :src="currentUser?.userAvatar" alt="头像" style="width: 100px; height: 100px;" />
    </div>
    <div>昵称：{{ currentUser?.userName }}</div>
    <div>ID：{{ currentUser?.userId }}</div>
    <div>角色：{{ currentUser?.role === 0 ? '学生' : '管理员' }}</div>
    <div>状态：{{ currentUser?.status === 0 ? '正常' : '封禁' }}</div>
    <div>发帖数：{{ currentUser?.userPostNum }}</div>
    <div>手机号：{{ currentUser?.userPhone }}</div>
    <div>邮箱：{{ currentUser?.userEmail }}</div>
  </div>
</template>