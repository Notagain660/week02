<template>
  <el-card>
    <div v-for="req in requests" :key="req.myId" class="request-item">
      <span>{{ req.userName }} 请求添加好友</span>
      <el-button type="primary" @click="accept(req.myId)">同意</el-button>
      <el-button @click="reject(req.myId)">拒绝</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRelationsByStatus, dealRelation } from '@/api/relation'
import { getOtherProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const requests = ref([])

const fetchRequests = async () => {
  const res = await getRelationsByStatus(1)
  if (res.code === 200) {
    const list = res.data
    const userIds = list.map(r => r.myId)
    const userInfos = await Promise.all(userIds.map(id => getOtherProfile(id)))
    requests.value = list.map((r, idx) => ({
      myId: r.myId,
      userName: userInfos[idx].data.nickname,
    }))
  }
}
const accept = async (userId) => {
  const res = await dealRelation(userId, 1, 1)
  if (res.code === 200) {
    ElMessage.success('已同意')
    await fetchRequests()
  }
}
const reject = async (userId) => {
  const res = await dealRelation(userId, 1, 0)
  if (res.code === 200) {
    ElMessage.success('已拒绝')
    await fetchRequests()
  }
}
onMounted(fetchRequests)
</script>