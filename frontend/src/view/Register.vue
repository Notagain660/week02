<script setup>
import {reactive, ref} from 'vue'
import { useStore } from '@/store'
import {useRouter} from "vue-router";

const router = useRouter()
const store = useStore()
const form = reactive({
  userPhone: '',
  userEmail: '',
  userName: '',
  password: ''
})
const confirmPassword = ref('')

const doRegister = async () => {

  if (form.password !== confirmPassword.value) {
    alert('两次密码不一致')
    return
  }
  const success = await store.register(form)
  if (success) {
    alert('注册成功，请登录')
    await router.push('/login')
  } else {
    alert('注册失败')
  }
}
</script>
<template>
  <div>
    <h1>注册</h1>
    <div><input type="text" v-model="form.userPhone" placeholder="手机号" /></div>
    <div><input type="text" v-model="form.userEmail" placeholder="邮箱" /></div>
    <div><input type="text" v-model="form.userName" placeholder="昵称" /></div>
    <div><input  type="password" v-model="form.password" placeholder="密码" /></div>
    <div><input type="password" v-model="confirmPassword" placeholder="确认密码" /></div>
    <button @click="doRegister">注册</button>
  </div>
</template>