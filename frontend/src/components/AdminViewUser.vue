<template>
  <div v-if="isAdmin" class="admin-view-user-btn">
    <label>用户ID：</label>
    <input type="number" v-model="userId" placeholder="输入用户ID" />
    <button @click="goToUserDetail" :disabled="!userId">查看用户信息</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '@/store'

const router = useRouter()
const store = useStore()
const userId = ref('')

const isAdmin = computed(() => store.currentUser?.role === 'ADMIN')

const goToUserDetail = () => {
  if (!userId.value) return
  router.push(`/admin/user/${userId.value}`)
}
</script>

<style scoped>
.admin-view-user-btn {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
}
</style>