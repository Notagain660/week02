<template>
  <div>
    <h1>登录</h1>
    <div>
      <input type="text" v-model="account" placeholder="手机号/邮箱" />
    </div>
    <div>
      <input type="password" v-model="password" placeholder="密码" />
    </div>
    <div>
      <button @click="doLogin">登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from '@/store'
import router from "@/router.js";

const account = ref('')      // 必须定义
const password = ref('')
const store = useStore()

ref(null);
const doLogin = async () => {
  try {
    const success = await store.login(account.value, password.value)
    if (success) {
       await router.push('/posts')
    } else {
      alert('登录失败')
    }
  } catch (err) {
    console.error(err)
    alert('请求出错')
  }
}
</script>

