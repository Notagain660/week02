<template>
  <div class="comment-item">
    <div class="comment-header">
      <el-avatar :size="32" :src="comment.userAvatar" />
      <span class="username">{{ comment.userName }}</span>
      <span class="floor">#{{ comment.floor }}</span>
      <span class="time">{{ formatTime(comment.replyTime) }}</span>
      <div class="actions">
        <el-button text @click="$emit('reply', comment.batchco, comment.commenterId)">回复</el-button>
        <el-button v-if="isOwner" text type="danger" @click="$emit('delete', comment.batchco)">删除</el-button>
        <el-button v-else text type="warning" @click="$emit('report', comment.batchco)">举报</el-button>
      </div>
    </div>
    <div class="comment-body">
      <span v-if="comment.replyId !== 0">回复 <span class="reply-target">#{{ getFloorByCommentId(comment.replyId) }}</span>：</span>
      {{ comment.commentText }}
    </div>
  </div>
</template>

<script setup>
defineEmits(['reply', 'delete', 'report'])
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const props = defineProps(['comment', 'getFloorByCommentId'])
const userStore = useUserStore()
const isOwner = computed(() => userStore.userInfo?.userId === props.comment.commenterId)
const formatTime = (t) => dayjs(t).format('MM-DD HH:mm')
</script>

<style scoped>
.comment-item {
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
.username {
  font-weight: bold;
}
.floor, .time {
  color: #999;
  font-size: 12px;
}
.actions {
  margin-left: auto;
}
.comment-body {
  margin-left: 40px;
  margin-top: 8px;
}
.reply-target {
  color: #409eff;
}
</style>