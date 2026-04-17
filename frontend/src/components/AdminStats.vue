<template>
  <button v-if="isAdmin" @click="goToStats" class="admin-stats-btn">
    管理统计
  </button>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '@/store'

const router = useRouter()
const store = useStore()
const isAdmin = computed(() => store.currentUser?.role === 'ADMIN')

const goToStats = () => {
  router.push('/admin/stats')
}
</script>

<style scoped>
.admin-stats-btn {
  padding: 6px 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.admin-stats-btn:hover {
  background-color: #66b1ff;
}
</style>