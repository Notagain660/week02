<template>
  <div class="post-detail" v-loading="loading">
    <el-card>
      <div class="post-header">
        <el-avatar :size="50" :src="post.userAvatar" />
        <div class="info">
          <div class="username">{{ post.userName }}</div>
          <div class="time">{{ formatTime(post.postTime) }}</div>
        </div>
        <div class="actions">
          <el-button v-if="isOwner" @click="editPost">编辑</el-button>
          <el-button v-if="isOwner" type="danger" @click="deletePost">删除</el-button>
          <el-button v-else type="warning" @click="reportPost">举报</el-button>
        </div>
      </div>
      <div class="post-content">
        <p><strong>{{ post.type === 0 ? '丢失' : '拾取' }}</strong> {{ post.itemName }}</p>
        <p>地点：{{ post.itemPlace }}</p>
        <p>时间：{{ formatTime(post.itemTime) }}</p>
        <p>描述：{{ post.userDescription }}</p>
        <p v-if="post.aiDescription">AI描述：{{ post.aiDescription }}</p>
        <img v-if="post.itemPhoto" :src="post.itemPhoto" style="max-width: 100%;" alt=""/>
        <p>联系方式：{{ post.contact }}</p>
        <p>可见范围：{{ visibleText }}</p>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <div class="comment-input">
        <el-input v-model="newComment" placeholder="发表评论" />
        <el-button type="primary" @click="submitComment">评论</el-button>
      </div>
      <CommentItem
          v-for="c in comments"
          :key="c.batchco"
          :comment="c"
          :get-floor-by-comment-id="getFloorByCommentId"
          @reply="startReply"
          @delete="deleteComment"
          @report="reportComment"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, deletePost as apiDeletePost } from '@/api/post'
import { addComment as apiAddComment, deleteComment as apiDeleteComment, getCommentList } from '@/api/comment'
import { addReport } from '@/api/report'
import { useUserStore } from '@/stores/user'
import CommentItem from '@/components/CommentItem.vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const postId = computed(() => Number(route.params.postId))
const post = ref({})
const comments = ref([])
const loading = ref(false)
const newComment = ref('')
const currentReplyId = ref(null)

const isOwner = computed(() => userStore.userInfo?.userId === post.value.posterId)
const visibleText = computed(() => {
  switch (post.value.visible) {
    case 0: return '所有人'
    case 1: return '仅自己'
    case 2: return '好友可见'
    default: return ''
  }
})

const fetchDetail = async () => {
  if (isNaN(postId.value)) {
    ElMessage.error('无效的帖子ID')
    return
  }
  loading.value = true
  try {
    const res = await getPostDetail(postId.value)
    if (res.code === 200) {
      post.value = res.data
      const commentRes = await getCommentList(postId.value, 1, 20)
      if (commentRes.code === 200) comments.value = commentRes.data.records
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const startReply = (commentId, commenterId) => {
  currentReplyId.value = commenterId
  newComment.value = `@${commentId} `
}

const submitComment = async () => {
  const replyId = currentReplyId.value !== null ? currentReplyId.value : post.value.posterId
  if (!newComment.value.trim()) return
  const res = await apiAddComment(postId.value, replyId, newComment.value)
  if (res.code === 200) {
    ElMessage.success('评论成功')
    newComment.value = ''
    currentReplyId.value = null
    await fetchDetail()
  }
}

const deleteComment = async (id) => {
  await ElMessageBox.confirm('确认删除？')
  const res = await apiDeleteComment(id)
  if (res.code === 200) ElMessage.success('删除成功')
  await fetchDetail()
}

const reportComment = (id) => {
  router.push({ path: '/report', query: { type: 2, contentId: id } })
}

const reportPost = () => router.push({ path: '/report', query: { type: 1, contentId: postId.value } })
const editPost = () => router.push(`/post/edit/${postId.value}`)
const deletePost = async () => {
  await ElMessageBox.confirm('确认删除帖子？')
  const res = await apiDeletePost(postId.value)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    await router.push('/')
  }
}
const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''
const getFloorByCommentId = (id) => {
  const comment = comments.value.find(c => c.batchco === id)
  return comment ? comment.floor : '?'
}

onMounted(fetchDetail)
</script>