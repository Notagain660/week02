<template>
  <div>
    <h2>我的好友</h2>
    <addFriend />
    <div v-if="loading">加载中...</div>
    <div v-else-if="friends.length === 0">暂无好友</div>
    <ul v-else>
      <li v-for="friend in friends" :key="friend.userId">
        {{ friend.userName }}
        <button @click="deleteFriend(friend.userId)">删除好友</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
console.log('FriendsList 组件已挂载')
import { ref, onMounted } from 'vue'
import { getRelation, dealRelation, getOtherProfile } from '@/api/user'
import { useStore } from '@/store'
import { ElMessage } from 'element-plus'
import addFriend from '@/components/addFriend.vue'

const store = useStore()
const friends = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    if (!store.currentUser) {
      await store.fetchUserInfo()
    }
    const currentUserId = Number(store.currentUser?.userId)
    if (!currentUserId) {
      ElMessage.error('请先登录')
      return
    }

    const res = await getRelation(2)
    if (res.code === 200) {
      const list = res.data
      const promises = list.map(async (item) => {
        const myId = Number(item.myId)
        const itsId = Number(item.itsId)
        const friendId = myId === currentUserId ? itsId : myId
        const userRes = await getOtherProfile(friendId)
        return {
          userId: friendId,
          userName: userRes.code === 200 ? userRes.data.nickname : '未知用户'
        }
      })
      friends.value = await Promise.all(promises)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}


const deleteFriend = async (userId) => {
  try {
    const res = await dealRelation(userId, 2, 2)   // act=2 删除好友
    if (res.code === 200) {
      ElMessage.success('已删除好友')
      await load()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('删除失败')
  }
}

onMounted(load)
</script>