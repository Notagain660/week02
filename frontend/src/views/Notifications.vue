<template>
  <div class="notification-icon" @click="goToNotifications">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0">
      <el-icon><Bell /></el-icon>
    </el-badge>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {Bell} from '@element-plus/icons-vue'
import {getRelationsByStatus} from '@/api/relation'

const router = useRouter()
const unreadCount = ref(0)

const fetchUnread = async () => {
  try {
    const friendRes = await getRelationsByStatus(1)
    unreadCount.value = friendRes.code === 200 ? friendRes.data.length : 0
  } catch (err) {
    console.error(err)
  }
}

const goToNotifications = () => {
  router.push('/friend-requests')
}

onMounted(fetchUnread)
</script>

<style scoped>
.notification-icon {
  cursor: pointer;
  margin: 0 10px;
}
</style>