<template>
  <div class="my-posts">
    <h3>我的帖子</h3>
    <div v-if="loading">加载中...</div>
    <div v-else-if="posts.length === 0">暂无帖子</div>
    <div v-else>
      <div v-for="post in posts" :key="post.postId" class="post-item">
        <div><strong>{{ post.type === 0 ? '丢失' : '拾取' }}</strong> {{ post.itemName }}</div>
        <div>地点：{{ post.itemPlace }}</div>
        <div>时间：{{ formatTime(post.itemTime) }}</div>
        <div>状态：{{ post.status === 0 ? '未处理' : '已完成' }}</div>
      </div>
      <div class="pagination">
        <button @click="prevPage" :disabled="page === 1">上一页</button>
        <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="nextPage" :disabled="page === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getPostList } from '@/api/user.js'
import { useStore } from '@/store'

const store = useStore()
const currentUserId = computed(() => store.currentUser?.userId)

const posts = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / size.value))

const fetchPosts = async () => {
  if (!currentUserId.value) return
  loading.value = true
  try {
    const res = await getPostList({
      pageCode: page.value,
      size: size.value,
      userId: currentUserId.value
    })
    if (res.code === 200) {
      posts.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const prevPage = () => {
  if (page.value > 1) {
    page.value--
    fetchPosts()
  }
}
const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++
    fetchPosts()
  }
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''

onMounted(fetchPosts)
</script>

<style scoped>
.my-posts {
  margin-top: 20px;
}
.post-item {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}
.pagination {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.pagination button {
  padding: 4px 10px;
  cursor: pointer;
}
</style>