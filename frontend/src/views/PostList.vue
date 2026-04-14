<template>
  <div class="post-list">
    <el-card class="filter-card">
      <el-form inline>
        <el-form-item label="类型">
          <el-select v-model="filters.type" clearable placeholder="全部">
            <el-option label="丢失" :value="0" />
            <el-option label="拾取" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部">
            <el-option label="未处理" :value="0" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="物品名称">
          <el-input v-model="filters.itemName" placeholder="物品名称" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="filters.itemPlace" placeholder="地点" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-loading="loading">
      <PostCard
          v-for="post in posts"
          :key="post.postId"
          :post="post"
          @click="goToDetail(post.postId)"
      />
      <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchPosts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/post'
import PostCard from '@/components/PostCard.vue'

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const filters = ref({
  type: undefined,
  status: undefined,
  itemName: '',
  itemPlace: '',
})

const fetchPosts = async () => {
  loading.value = true
  const res = await getPostList({
    pageCode: page.value,
    size: size.value,
    type: filters.value.type,
    status: filters.value.status,
    itemName: filters.value.itemName,
    itemPlace: filters.value.itemPlace,
  })
  if (res.code === 200) {
    posts.value = res.data.records
    total.value = res.data.total
  }
  loading.value = false
}
const search = () => {
  page.value = 1
  fetchPosts()
}
const resetFilters = () => {
  filters.value = { type: undefined, status: undefined, itemName: '', itemPlace: '' }
  search()
}
const goToDetail = (id) => router.push(`/post/${id}`)

onMounted(fetchPosts)
</script>

<style scoped>
.post-list {
  max-width: 1000px;
  margin: 0 auto;
}
.filter-card {
  margin-bottom: 20px;
}
</style>