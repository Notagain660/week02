<template>
  <div>
    <h1>修改邮箱</h1>
    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="旧密码" required>
        <el-input type="password" v-model="form.oldPassword" />
      </el-form-item>
      <el-form-item label="手机号" required>
        <el-input v-model="form.currentPhone" placeholder="当前绑定手机号" />
      </el-form-item>
      <el-form-item label="当前邮箱" required>
        <el-input v-model="form.email" placeholder="当前绑定邮箱" />
      </el-form-item>
      <el-form-item label="新邮箱" required>
        <el-input v-model="form.newEmail
" placeholder="新邮箱" />
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
  currentPhone: '',
  email: '',
  newEmail: ''
})

const submit = async () => {
  if (!form.oldPassword || !form.currentPhone || !form.email || !form.newEmail) {
    ElMessage.error('请填写完整信息')
    return
  }
  if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(form.newEmail)) {
    ElMessage.error('新邮箱格式不正确')
    return
  }

  const success = await store.updateEmail({
    str: form.newEmail,               // 新手机号
    passerword: form.oldPassword,
    phoner: form.currentPhone,        // 当前手机号（用于验证）
    emailer: form.email               // 邮箱（用于验证）
  })
  if (success) {
    ElMessage.success('邮箱修改成功，请重新登录')
    store.logout()
    await router.push('/login')
  } else {
    ElMessage.error('修改失败，请检查信息')
  }
}
</script>