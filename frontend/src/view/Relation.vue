<template>
  <div>
    <h2>好友申请</h2>
    <div style="margin-bottom: 20px;">
      <input v-model="targetUserId" placeholder="输入用户ID" />
      <button @click="goToUser">查看他人信息</button>
    </div>
    <addFriend />
    <div v-if="requests.length === 0">暂无申请</div>
    <ul v-else>
      <li v-for="req in requests" :key="req.myId">
        {{ req.userName }} 请求加好友
        <button @click="accept(req.myId)">同意</button>
        <button @click="reject(req.myId)">拒绝</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOtherProfile, dealRelation, getRelation} from '@/api/user'
import {useStore} from "@/store.js";
import addFriend from  "@/components/addFriend.vue"
import router from "@/router.js";

const requests = ref([])
const store = useStore()
const targetUserId = ref('')

const goToUser = () => {
  if (!targetUserId.value) return
  router.push(`/user/${targetUserId.value}`)
}


const load = async () => {
  const res = await getRelation(1)  // 1 = 申请中
  if (res.code === 200) {
    const list = res.data
    // 获取申请人昵称
    for (let i = 0; i < list.length; i++) {
      const userRes = await getOtherProfile(list[i].myId)
      if (userRes.code === 200) {
        list[i].userName = userRes.data.nickname
      } else {
        list[i].userName = '用户'
      }
    }
    requests.value = list
  }
}

const accept = async (userId) => {
  await dealRelation(userId, 1, 1)  // 同意
  await load()
}
const reject = async (userId) => {
  await dealRelation(userId, 1, 0)  // 拒绝
  await load()
}

onMounted(load)
</script>