<template>
  <div>
    <h1>修改密码</h1>
    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="旧密码" required>
        <el-input type="password" v-model="form.oldPassword" />
      </el-form-item>
      <el-form-item label="手机号" required>
        <el-input v-model="form.phone" placeholder="当前绑定手机号" />
      </el-form-item>
      <el-form-item label="邮箱" required>
        <el-input v-model="form.email" placeholder="当前绑定邮箱" />
      </el-form-item>
      <el-form-item label="新密码" required>
        <el-input type="password" v-model="form.newPassword" />
      </el-form-item>
      <el-form-item label="确认新密码" required>
        <el-input type="password" v-model="form.confirmPassword" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useStore } from '@/store'

const router = useRouter()
const store = useStore()

const form = reactive({
  oldPassword: '',
  phone: '',
  email: '',
  newPassword: '',
  confirmPassword: ''
})

const submit = async () => {
  // 简单校验
  if (!form.oldPassword || !form.phone || !form.email || !form.newPassword || !form.confirmPassword) {
    ElMessage.error('请填写完整信息')
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  const success = await store.updatePassword({
    str: form.newPassword,
    passerword: form.oldPassword,
    phoner: form.phone,
    emailer: form.email
  })
  if (success) {
    ElMessage.success('密码修改成功，请重新登录')
    store.logout()
    await router.push('/login')
  } else {
    ElMessage.error('修改失败，请检查信息')
  }
}
</script>