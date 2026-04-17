<template>
  <div>
    <!-- 点击按钮显示输入框 -->
    <button v-if="!showInput" @click="openInput">标记已读</button>
    <div v-else>
      <input
          v-model="chatId"
          @keyup.enter="markRead"
          @blur="markRead"
          ref="inputRef"
          placeholder="输入消息ID"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { markChatRead } from '@/api/user.js'   // 请确认路径正确

const showInput = ref(false)
const chatId = ref('')
const inputRef = ref(null)

const markRead = async () => {
  const trimmed = chatId.value?.trim()
  if (!trimmed) {
    showInput.value = false
    return
  }
  try {
    const res = await markChatRead(Number(trimmed))
    if (res.code === 200) {
      ElMessage.success('已标记为已读')
    } else {
      ElMessage.error(res.message || '操作失败')
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