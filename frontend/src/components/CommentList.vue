<template>
  <div>
    <h3>评论</h3>
    <div v-if="loading">加载中...</div>
    <div v-else-if="comments.length === 0">暂无评论</div>
    <ul v-else>
      <li v-for="c in comments" :key="c.batchco" style="border-bottom:1px solid #eee; padding:8px 0;">
        <div>评论ID：{{ c.batchco }}</div>
        <div>
          <strong>用户 {{ c.commenterId }}</strong> <span style="font-size:12px; color:#999;">(楼层 {{ c.floor }})</span>
          <span style="margin-left:10px; font-size:12px;">{{ formatTime(c.replyTime) }}</span>
        </div>
        <div>{{ c.commentText }}</div>
        <div v-if="c.replyId !== 0" style="font-size:12px; color:#666;">回复 #{{ getReplyFloor(c.replyId) }}</div>
      </li>
    </ul>
    <!-- 分页 -->
    <div v-if="total > size">
      <button :disabled="page === 1" @click="changePage(page-1)">上一页</button>
      第 {{ page }} 页 / 共 {{ totalPages }} 页
      <button :disabled="page === totalPages" @click="changePage(page+1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getCommentList } from '@/api/user.js'

const props = defineProps({
  postId: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

const comments = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(props.pageSize)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / size.value))

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await getCommentList(props.postId, page.value, size.value)
    if (res.code === 200) {
      comments.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const changePage = (newPage) => {
  page.value = newPage
  fetchComments()
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''

// 根据回复的评论ID获取楼层号
const getReplyFloor = (replyId) => {
  const replied = comments.value.find(c => c.batchco === replyId)
  return replied ? replied.floor : '?'
}

onMounted(fetchComments)
</script>