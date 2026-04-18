<template>
  <div v-if="isAdmin">
    <button @click="showForm = true">解封内容</button>
    <div v-if="showForm">
      <select v-model="type">
        <option :value="0">用户</option>
        <option :value="1">帖子</option>
        <option :value="2">评论</option>
      </select>
      <input v-model="contentId" type="number" placeholder="内容ID" />
      <button @click="submit" :disabled="loading">{{ loading ? '提交中' : '解封' }}</button>
      <button @click="showForm = false">取消</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deblockContent } from '@/api/user'  // 需要添加此 API 函数
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
    await ElMessageBox.confirm('确定要解封吗？', '提示', { type: 'info' })
    loading.value = true
    const res = await deblockContent(type.value, Number(contentId.value))
    if (res.code === 200) {
      ElMessage.success('解封成功')
      emit('success')
      showForm.value = false
      contentId.value = ''
    } else {
      ElMessage.error(res.message || '解封失败')
    }
  } catch (err) {
    if (err !== 'cancel') console.error(err)
  } finally {
    loading.value = false
  }
}
</script>