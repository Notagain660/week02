<template>
  <div>
    <button @click="callAi" :disabled="loading">
      {{ loading ? '生成中...' : 'AI 描述' }}
    </button>
    <div v-if="result">{{ result }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateAiDescription } from '@/api/user.js'

const props = defineProps({
  itemName: String,
  place: String,
  userDesc: String
})
const emit = defineEmits(['success'])

const loading = ref(false)
const result = ref('')

const callAi = async () => {
  if (!props.itemName || !props.place) {
    ElMessage.warning('请先填写物品名称和地点')
    return
  }
  loading.value = true
  try {
    const res = await generateAiDescription({
      itemName: props.itemName,
      place: props.place,
      userDesc: props.userDesc || ''
    })
    if (res.code === 200) {
      result.value = res.data
      emit('success', res.data)
    } else {
      ElMessage.error(res.message || '生成失败')
    }
  } catch (error) {
    ElMessage.error('请求出错')
  } finally {
    loading.value = false
  }
}
</script>