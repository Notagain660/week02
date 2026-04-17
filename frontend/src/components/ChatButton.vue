<template>
  <button @click="goToChat" class="chat-btn">
    发消息
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const router = useRouter()

const goToChat = () => {
  router.push(`/chat/${props.userId}`)
}
</script>

<style scoped>
.chat-btn {
  margin-left: 10px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
}
.chat-btn:hover {
  background-color: #66b1ff;
}
</style>