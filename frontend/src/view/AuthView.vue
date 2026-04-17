<template>
  <div class="auth-container">
    <el-tabs v-model="activeTab" class="auth-tabs">
      <el-tab-pane label="登录" name="login">
        <Login />
      </el-tab-pane>
      <el-tab-pane label="注册" name="register">
        <Register />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Login from './user.vue'
import Register from './Register.vue'

const activeTab = ref('login')
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}
.auth-tabs {
  width: 450px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>