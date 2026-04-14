<template>
  <el-card>
    <div class="avatar-center">
      <el-avatar :size="100" :src="otherInfo?.avatar" />
    </div>
    <div class="nickname">{{ otherInfo?.nickname }}</div>
    <div class="status">状态: {{ otherInfo?.status === 0 ? '正常' : '封禁' }}</div>
    <div class="post-num">发帖数: {{ otherInfo?.postNum }}</div>
    <div class="actions">
      <el-button v-if="!isFriend" type="primary" @click="addFriend">添加好友</el-button>
      <el-button v-else type="success" @click="sendMessage">发送消息</el-button>
      <el-button v-else type="danger" @click="deleteFriend">删除好友</el-button>
    </div>
    <div class="posts">
      <PostCard v-for="p in userPosts" :key="p.postId" :post="p" @click="goToPost(p.postId)" />
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOtherProfile } from '@/api/user'
import { sendFriendRequest, getRelationsByStatus, dealRelation } from '@/api/relation'
import { getPostList } from '@/api/post'
import { useUserStore } from '@/stores/user'
import PostCard from '@/components/PostCard.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userId = Number(route.params.userId)
const otherInfo = ref(null)
const isFriend = ref(false)
const userPosts = ref([])

const fetchData = async () => {
  const res = await getOtherProfile(userId)
  if (res.code === 200) otherInfo.value = res.data
  // 检查好友关系
  const friendRes = await getRelationsByStatus(2)
  if (friendRes.code === 200) {
    isFriend.value = friendRes.data.some(r =>
        (r.myId === userStore.userInfo?.userId && r.itsId === userId) ||
        (r.myId === userId && r.itsId === userStore.userInfo?.userId)
    )
  }
  const postsRes = await getPostList({ pageCode: 1, size: 20 })
  if (postsRes.code === 200) {
    userPosts.value = postsRes.data.records.filter(p => p.posterId === userId)
  }
}
const addFriend = async () => {
  const res = await sendFriendRequest(userId)
  if (res.code === 200) ElMessage.success('已发送申请')
}
const deleteFriend = async () => {
  const res = await dealRelation(userId, 2, 2)
  if (res.code === 200) {
    ElMessage.success('已删除好友')
    isFriend.value = false
  }
}
const sendMessage = () => router.push(`/chat/${userId}`)
const goToPost = (id) => router.push(`/post/${id}`)
onMounted(fetchData)
</script>

<style scoped>
.avatar-center {
  text-align: center;
}
.nickname {
  text-align: center;
  font-size: 24px;
  margin: 10px 0;
}
.actions {
  text-align: center;
  margin: 20px 0;
}
</style>