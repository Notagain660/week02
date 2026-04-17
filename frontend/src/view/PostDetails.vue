<template>
  <div>
    <div v-if="loading">加载中...</div>
    <div v-else-if="post">
      <h2>帖子详情</h2>
      <div><strong>类型：</strong>{{ post.type === 0 ? '丢失' : '拾取' }}</div>
      <div><strong>发布者：</strong>{{ post.posterId}}</div>
      <div><strong>帖子id：</strong>{{ post.postId}}</div>
      <div><strong>物品名称：</strong>{{ post.itemName }}</div>
      <div><strong>地点：</strong>{{ post.itemPlace }}</div>
      <div><strong>时间：</strong>{{ formatTime(post.itemTime) }}</div>
      <div><strong>描述：</strong>{{ post.userDescription }}</div>
      <div><strong>联系方式：</strong>{{ post.contact }}</div>
      <div v-if="post.aiDescription"><strong>AI描述：</strong>{{ post.aiDescription }}</div>
      <div><strong>可见范围：</strong>{{ visibleText }}</div>
      <div><strong>状态：</strong>{{ statusText }}</div>
      <div v-if="post.itemPhoto">
        <img :src="post.itemPhoto" style="max-width:300px" alt="帖子图片" />
      </div>
      <div><strong>发布时间：</strong>{{ formatTime(post.postTime) }}</div>
      <div><strong>是否置顶：</strong>{{ post.pinOrNot === false ? '未置顶' : '已置顶'}}</div>

      <div v-if="commentLoading">加载评论中...</div>
      <div v-else-if="comments.length === 0">暂无评论</div>
    </div>
    <div v-else>帖子不存在</div>
    <CommentList :post-id="Number(postId)" />

    <button @click="router.push(`/post/edit/${postId}`)">编辑</button>
    <PostCommentButton :post-id="postId" @success="refreshComments" />
    <DeleteCommentButton @success="refreshComments" />
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import { useRoute } from 'vue-router'
import { getPostDetail, getCommentList } from '@/api/user.js'
import CommentList from "@/components/CommentList.vue";
import router from "@/router.js";
import PostCommentButton from "@/components/PostCommentButton.vue";
import DeleteCommentButton from "@/components/DeleteCommentButton.vue";

const route = useRoute()
const postId = Number(route.params.postId)
const post = ref(null)
const loading = ref(false)
const comments = ref([])
const commentLoading = ref(false)


const refreshComments = () => {
  // 重新加载评论列表
  fetchComments()
}


const visibleText = computed(() => {
  switch (post.value?.visible) {
    case 'ALLSEE': return '所有人可见'
    case 'MESEEONLY': return '仅自己可见'
    case 'FRIENDSEE': return '好友可见'
    default: return '未知'
  }
})
const statusText = computed(() => {
  switch (post.value?.postStatus) {
    case 'UNFINISHED': return '未处理'
    case 'BLOCKED': return '封禁'
    case 'DELETED': return '已删除'
    case 'COMPLETED': return '已完成'
    default: return '未知'
  }
})
const formatTime = (t) => t ? new Date(t).toLocaleString() : ''

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getPostDetail(postId)
    if (res.code === 200) {
      post.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  commentLoading.value = true
  try {
    const res = await getCommentList(postId, 1, 100)  // 取前100条评论
    if (res.code === 200) {
      comments.value = res.data.records
    }
  } catch (error) {
    console.error(error)
  } finally {
    commentLoading.value = false
  }
}

onMounted(() => {
  fetchDetail()
  fetchComments()
})
</script>