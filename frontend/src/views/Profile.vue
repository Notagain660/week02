<template>
  <div class="profile">
    <el-card>
      <div class="avatar-center">
        <el-avatar :size="100" :src="userInfo?.userAvatar" />
      </div>
      <div class="nickname">{{ userInfo?.userName }}</div>
      <div class="user-id">ID: {{ userInfo?.userId }}</div>
      <div class="status">状态: {{ userInfo?.status === 0 ? '正常' : '封禁' }}</div>
      <div class="post-num">发帖数: {{ userInfo?.userPostNum }}</div>
      <div class="tabs">
        <el-button :type="activeTab === 0 ? 'primary' : ''" @click="activeTab = 0">隐私信息</el-button>
        <el-button :type="activeTab === 1 ? 'primary' : ''" @click="activeTab = 1">我的帖子</el-button>
      </div>
      <div v-if="activeTab === 0">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="角色">{{ userInfo?.role === 0 ? '学生' : '管理员' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userInfo?.userPhone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userInfo?.userEmail }}</el-descriptions-item>
          <el-descriptions-item label="密码">********</el-descriptions-item>
        </el-descriptions>
        <el-button type="primary" @click="$router.push('/profile/edit')">编辑资料</el-button>
      </div>
      <div v-else>
        <PostCard v-for="p in myPosts" :key="p.postId" :post="p" @click="goToPost(p.postId)" />
        <el-pagination v-model:current-page="page" :total="total" layout="prev, pager, next" @current-change="fetchMyPosts" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {createRouter as $router, useRouter} from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getPostList } from '@/api/post'
import PostCard from '@/components/PostCard.vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const activeTab = ref(0)
const myPosts = ref([])
const page = ref(1)
const total = ref(0)

const fetchMyPosts = async () => {
  const res = await getPostList({ pageCode: page.value, size: 10 })
  if (res.code === 200) {
    myPosts.value = res.data.records.filter(p => p.posterId === userInfo.value?.userId)
    total.value = myPosts.value.length
  }
}
const goToPost = (id) => router.push(`/post/${id}`)
onMounted(fetchMyPosts)
</script>

<style scoped>
.profile {
  max-width: 800px;
  margin: 0 auto;
}
.avatar-center {
  text-align: center;
}
.nickname {
  text-align: center;
  font-size: 24px;
  margin: 10px 0;
}
.user-id {
  text-align: center;
  color: #999;
}
.status, .post-num {
  text-align: center;
}
.tabs {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}
</style>