<template>
  <div>
    <!-- 点击按钮显示输入框 -->
    <button v-if="!showInput" @click="openInput">发送消息</button>
    <div v-else>
      <input
          v-model="messageContent"
          @keyup.enter="send"
          @blur="send"
          ref="inputRef"
          placeholder="输入消息内容"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { sendChat } from '@/api/user.js'

const props = defineProps({
  receiverId: {
    type: Number,
    required: true
  }
})
const emit = defineEmits(['success'])

const showInput = ref(false)
const messageContent = ref('')
const inputRef = ref(null)

const send = async () => {
  console.log('receiverId:', props.receiverId, typeof props.receiverId)
  const trimmed = messageContent.value?.trim()
  if (!trimmed) {
    showInput.value = false
    return
  }
  try {
    const res = await sendChat(props.receiverId, trimmed)
    if (res.code === 200) {
      ElMessage.success('发送成功')
      emit('success')   // 通知父组件刷新消息列表
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
  showInput.value = false
  messageContent.value = ''
}

const openInput = () => {
  showInput.value = true
  nextTick(() => inputRef.value?.focus())
}
</script>