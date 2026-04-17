<template>
  <div>
    <div>
      <label>回复用户ID：</label>
      <input v-model="replyId" type="number" placeholder="被回复的用户ID（0表示直接回复帖子）" />
    </div>
    <div>
      <label>评论内容：</label>
      <input v-model="content" placeholder="输入评论内容" />
    </div>
    <button @click="submit">发表评论</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addComment } from '@/api/user.js'

const props = defineProps({
  postId: {
    type: Number,
    required: true
  }
})
const emit = defineEmits(['success'])

const replyId = ref(0)
const content = ref('')

const submit = async () => {
  const trimmed = content.value.trim()
  if (!trimmed) {
    ElMessage.error('请输入评论内容')
    return
  }
  const finalReplyId = replyId.value === null ? 0 : Number(replyId.value)
  try {
    const res = await addComment(props.postId, finalReplyId, trimmed)
    if (res.code === 200) {
      ElMessage.success('评论成功')
      content.value = ''
      replyId.value = 0
      emit('success')
    } else {
      ElMessage.error(res.message || '评论失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
}
</script>