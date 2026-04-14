<template>
  <div class="notification-icon" @click="goToNotifications">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0">
      <el-icon><Bell /></el-icon>
    </el-badge>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { getRelationsByStatus } from '@/api/relation'
import { getRecentChats } from '@/api/chat'

const router = useRouter()
const unreadCount = ref(0)

const fetchUnread = async () => {
  try {
    // 获取未处理的好友申请数量
    const friendRes = await getRelationsByStatus(1)
    const friendCount = friendRes.code === 200 ? friendRes.data.length : 0
    // 获取未读消息数量（需要后端支持统计未读消息数，暂简化）
    // 由于没有获取未读消息总数的接口，可以从聊天列表计算或后端新增接口
    // 这里先用好友申请数演示
    unreadCount.value = friendCount
  } catch (err) {
    console.error(err)
  }
}

const goToNotifications = () => {
  router.push('/friend-requests')  // 或跳转到消息列表
}

onMounted(fetchUnread)
</script>

<style scoped>
.notification-icon {
  cursor: pointer;
  margin: 0 10px;
}
</style>