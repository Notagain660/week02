<template>
  <div v-if="isAdmin">
    <button v-if="!showForm" @click="showForm = true">处理举报</button>
    <div v-else>
      <select v-model="status">
        <option :value="0">未处理</option>
        <option :value="1">处理中</option>
        <option :value="2">已处理</option>
      </select>
      <button @click="submit" :disabled="loading">{{ loading ? '提交中...' : '提交' }}</button>
      <button @click="showForm = false">取消</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { dealReport } from '@/api/user.js'
import { useStore } from '@/store'

const props = defineProps({
  reportId: {
    type: Number,
    required: true
  }
})
const emit = defineEmits(['success'])

const store = useStore()
const isAdmin = computed(() => store.currentUser?.role === 'ADMIN')

const showForm = ref(false)
const status = ref(2)  // 默认已处理
const loading = ref(false)

const submit = async () => {
  loading.value = true
  try {
    const res = await dealReport(props.reportId, status.value)
    if (res.code === 200) {
      ElMessage.success('处理成功')
      emit('success')
      showForm.value = false
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  } finally {
    loading.value = false
  }
}
</script>