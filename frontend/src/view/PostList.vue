<template>
  <div>
    <button @click="goToMyProfile">我的主页</button>
    <button @click="goToMyReport">举报管理</button>
    <AdminStats />
    <button @click="router.push('/notifications')">消息中心</button>
    <h2>全部帖子</h2>
    <router-link to="/post/create">发布帖子</router-link>
    <div class="admin-friend-tip">
      ⚡ 进站同学请先添加管理员好友（ID：2 和 ID：3）⚡
    </div>
    <!-- 筛选栏 -->
    <div>
      <select v-model="filters.type">
        <option :value="undefined">全部类型</option>
        <option :value="0">丢失</option>
        <option :value="1">拾取</option>
      </select>
      <select v-model="filters.status">
        <option :value="undefined">全部状态</option>
        <option :value="0">未处理</option>
        <option :value="3">已完成</option>
      </select>
      <input v-model="filters.itemName" placeholder="物品名称" />
      <input v-model="filters.itemPlace" placeholder="地点" />
      <button @click="search">搜索</button>
      <button @click="resetFilters">重置</button>
    </div>

    <!-- 帖子列表 -->
    <div v-if="loading">加载中...</div>
    <div v-else-if="posts.length === 0">暂无帖子</div>
    <ul v-else>
      <li v-for="post in posts" :key="post.postId" style="border-bottom:1px solid #ccc; margin-bottom:10px;">
        <div><strong>{{ post.type === 'LOST' ? '丢失' : '拾取' }}</strong> {{ post.itemName }}</div>
        <div>地点：{{ post.itemPlace }}</div>
        <div>状态：{{ getStatusText(post.status) }}</div>
        <button @click="goToDetail(post.postId)">查看详情</button>
        <PinButton :post-id="post.postId" :pin-status="post.pinOrNot" />
      </li>
    </ul>

    <!-- 分页 -->
    <div>
      <button :disabled="page === 1" @click="changePage(page-1)">上一页</button>
      <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
      <button :disabled="page === totalPages" @click="changePage(page+1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/user.js'
import PinButton from "@/components/PinButton.vue";
import AdminStats from "@/components/AdminStats.vue";

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const filters = reactive({
  type: undefined,
  status: undefined,
  itemName: '',
  itemPlace: ''
})

const totalPages = computed(() => Math.ceil(total.value / size.value))

const goToMyProfile = () => {
  router.push('/user/checkme')
}

const goToMyReport = () => {
  router.push('/report/check')
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = {
      pageCode: page.value,
      size: size.value,
      ...filters
    }
    // 过滤掉 undefined 或空字符串的参数
    Object.keys(params).forEach(key => {
      if (params[key] === undefined || params[key] === '') delete params[key]
    })
    const res = await getPostList(params)
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

const search = () => {
  page.value = 1
  fetchPosts()
}

const resetFilters = () => {
  filters.type = undefined
  filters.status = undefined
  filters.itemName = ''
  filters.itemPlace = ''
  search()
}

const changePage = (newPage) => {
  page.value = newPage
  fetchPosts()
}

const goToDetail = (postId) => {
  router.push(`/post/${postId}`)
}

const getStatusText = (status) => {
  switch (status) {
    case 'UNFINISHED': return '未处理'
    case 'COMPLETED': return '已完成'
    default: return '未知'
  }
}

onMounted(fetchPosts)
</script>