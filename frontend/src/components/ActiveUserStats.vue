<template>
  <div class="active-user-stats">
    <label>开始日期：</label>
    <input type="date" v-model="startDate" />
    <label>结束日期：</label>
    <input type="date" v-model="endDate" />
    <button @click="fetchActiveCount" :disabled="loading">查询</button>
    <div v-if="loading">加载中...</div>
    <div v-else-if="activeCount !== null">活跃用户数：{{ activeCount }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getActiveUsers } from '@/api/user.js'

const startDate = ref('')
const endDate = ref('')
const activeCount = ref(null)
const loading = ref(false)

const fetchActiveCount = async () => {
  if (!startDate.value || !endDate.value) {
    ElMessage.error('请选择开始和结束日期')
    return
  }
  loading.value = true
  try {
    const res = await getActiveUsers(startDate.value, endDate.value)
    if (res.code === 200) {
      activeCount.value = res.data
    } else {
      ElMessage.error(res.message || '获取失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.active-user-stats {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.active-user-stats label {
  margin-right: 5px;
  margin-left: 10px;
}
.active-user-stats input {
  margin-right: 15px;
}
.active-user-stats button {
  margin-left: 10px;
}
</style>