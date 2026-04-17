<template>
  <div>
    <button v-if="!showInput" @click="openInput">删除评论</button>
    <div v-else>
      <input
          v-model="commentId"
          @keyup.enter="handleDelete"
          @blur="handleDelete"
          ref="inputRef"
          placeholder="输入评论ID"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { deleteComment as apiDeleteComment } from '@/api/user.js'  // 别名

const emit = defineEmits(['success'])
const showInput = ref(false)
const commentId = ref('')
const inputRef = ref(null)

const handleDelete = async () => {
  const trimmed = commentId.value?.trim()
  if (!trimmed) {
    showInput.value = false
    return
  }
  const id = Number(trimmed)
  if (isNaN(id)) {
    ElMessage.error('请输入有效的数字ID')
    showInput.value = false
    return
  }
  try {
    const res = await apiDeleteComment(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      emit('success')
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
  showInput.value = false
  commentId.value = ''
}

const openInput = () => {
  showInput.value = true
  nextTick(() => inputRef.value?.focus())
}
</script>