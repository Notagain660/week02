<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo">校园AI失物招领</div>
      <div class="nav">
        <el-button text @click="$router.push('/')">首页</el-button>
        <el-button text @click="$router.push('/profile')">我的</el-button>
        <el-button text @click="$router.push('/chats')">消息</el-button>
        <el-button text @click="$router.push('/friend-requests')">好友申请</el-button>
        <el-button text @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409eff;
  color: white;
  padding: 0 20px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
}
.nav .el-button {
  color: white;
}
.main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>