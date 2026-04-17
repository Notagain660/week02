<template>
  <div v-if="isAdmin" class="admin-delete">
    <label>类型：</label>
    <select v-model="deleteType">
      <option value="1">帖子</option>
      <option value="2">评论</option>
    </select>
    <label>ID：</label>
    <input type="number" v-model="deleteId" placeholder="内容ID" />
    <button @click="handleDelete" :disabled="loading">删除</button>
    <span v-if="message" :class="messageType">{{ message }}</span>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import { adminDelete } from '@/api/user.js'
import { useStore } from '@/store'

const store = useStore()
const isAdmin = computed(() => store.currentUser?.role === 'ADMIN')

const deleteType = ref('1')
const deleteId = ref('')
const loading = ref(false)
const message = ref('')
const messageType = ref('')

const handleDelete = async () => {
  if (!deleteId.value) {
    message.value = '请输入ID'
    messageType.value = 'error'
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除类型为${deleteType.value === '1' ? '帖子' : '评论'}、ID为${deleteId.value}的内容？`, '提示', {
      type: 'warning'
    })
    loading.value = true
    const res = await adminDelete(Number(deleteType.value), Number(deleteId.value))
    if (res.code === 200) {
      message.value = '删除成功'
      messageType.value = 'success'
      deleteId.value = ''
    } else {
      message.value = res.message || '删除失败'
      messageType.value = 'error'
    }
  } catch (error) {
    if (error !== 'cancel') {
      message.value = '请求出错'
      messageType.value = 'error'
      console.error(error)
    }
  } finally {
    loading.value = false
    setTimeout(() => { message.value = '' }, 3000)
  }
}
</script>

<style scoped>
.admin-delete {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.admin-delete label {
  margin-right: 5px;
  margin-left: 10px;
}
.admin-delete input, .admin-delete select {
  margin-right: 10px;
}

</style>