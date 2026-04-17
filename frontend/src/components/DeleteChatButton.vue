<template>
  <div>
    <!-- 点击按钮显示输入框 -->
    <button v-if="!showInput" @click="openInput">删除消息</button>
    <div v-else>
      <input
          v-model="chatId"
          @keyup.enter="deleteMsg"
          @blur="deleteMsg"
          ref="inputRef"
          placeholder="输入消息ID"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { deleteChat } from '@/api/user'   // 根据你的API位置调整

const showInput = ref(false)
const chatId = ref('')
const inputRef = ref(null)

const deleteMsg = async () => {
  const trimmed = chatId.value?.trim()
  if (!trimmed) {
    showInput.value = false
    return
  }
  try {
    const res = await deleteChat(Number(trimmed))
    if (res.code === 200) {
      ElMessage.success('删除成功')
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
  showInput.value = false
  chatId.value = ''
}

const openInput = () => {
  showInput.value = true
  nextTick(() => inputRef.value?.focus())
}
</script>