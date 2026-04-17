<template>
  <div v-if="isAdmin">
    <button @click="showForm = true">封锁内容</button>
    <div v-if="showForm">
      <select v-model="type">
        <option :value="0">用户</option>
        <option :value="1">帖子</option>
        <option :value="2">评论</option>
      </select>
      <input v-model="contentId" type="number" placeholder="内容ID" />
      <button @click="submit" :disabled="loading">{{ loading ? '提交中' : '提交' }}</button>
      <button @click="showForm = false">取消</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { blockContent } from '@/api/user.js'
import { useStore } from '@/store'

const store = useStore()
const isAdmin = computed(() => store.currentUser?.role === 'ADMIN')
const showForm = ref(false)
const type = ref(0)
const contentId = ref('')
const loading = ref(false)
const emit = defineEmits(['success'])

const submit = async () => {
  if (!contentId.value) {
    ElMessage.error('请输入内容ID')
    return
  }
  try {
    await ElMessageBox.confirm('确定要封锁吗？', '提示', { type: 'warning' })
    loading.value = true
    const res = await blockContent(type.value, Number(contentId.value))
    if (res.code === 200) {
      ElMessage.success('封锁成功')
      emit('success')
      showForm.value = false
      contentId.value = ''
    } else {
      ElMessage.error(res.message || '失败')
    }
  } catch (err) {
    if (err !== 'cancel') console.error(err)
  } finally {
    loading.value = false
  }
}
</script>