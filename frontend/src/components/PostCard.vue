<template>
  <el-card class="post-card" @click="$emit('click')">
    <div class="post-header">
      <el-avatar :size="40" :src="post.userAvatar || '/default-avatar.png'" />
      <div class="post-info">
        <div class="username">{{ post.userName }}</div>
        <div class="time">{{ formatTime(post.postTime) }}</div>
      </div>
    </div>
    <div class="post-body">
      <span class="type-badge" :class="{ lost: post.type === 0, find: post.type === 1 }">
        {{ post.type === 0 ? '丢失' : '拾取' }}
      </span>
      <span class="item-name">{{ post.itemName }}</span>
      <span class="place">{{ post.itemPlace }}</span>
    </div>
  </el-card>
</template>

<script setup>
import dayjs from 'dayjs'

defineProps(['post'])
defineEmits(['click'])

const formatTime = (t) => dayjs(t).format('MM-DD HH:mm')
</script>

<style scoped>
.post-card {
  margin-bottom: 16px;
  cursor: pointer;
}
.post-header {
  display: flex;
  align-items: center;
}
.post-info {
  margin-left: 12px;
}
.username {
  font-weight: bold;
}
.time {
  font-size: 12px;
  color: #999;
}
.post-body {
  margin-top: 12px;
}
.type-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 8px;
}
.type-badge.lost {
  background-color: #f56c6c;
  color: white;
}
.type-badge.find {
  background-color: #67c23a;
  color: white;
}
.item-name {
  font-weight: bold;
  margin-right: 12px;
}
.place {
  color: #666;
}
</style>