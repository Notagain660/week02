<template>
  <div>
    <h1>修改手机号</h1>
    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="旧密码" required>
        <el-input type="password" v-model="form.oldPassword" />
      </el-form-item>
      <el-form-item label="当前手机号" required>
        <el-input v-model="form.currentPhone" placeholder="当前绑定手机号" />
      </el-form-item>
      <el-form-item label="邮箱" required>
        <el-input v-model="form.email" placeholder="当前绑定邮箱" />
      </el-form-item>
      <el-form-item label="新手机号" required>
        <el-input v-model="form.newPhone" placeholder="新手机号" />
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
  newPhone: ''
})

const submit = async () => {
  if (!form.oldPassword || !form.currentPhone || !form.email || !form.newPhone) {
    ElMessage.error('请填写完整信息')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(form.newPhone)) {
    ElMessage.error('新手机号格式不正确')
    return
  }
  // 调用 store 中的 updatePhone action，参数按照后端 SecurityDTO 格式
  const success = await store.updatePhone({
    str: form.newPhone,               // 新手机号
    passerword: form.oldPassword,
    phoner: form.currentPhone,        // 当前手机号（用于验证）
    emailer: form.email               // 邮箱（用于验证）
  })
  if (success) {
    ElMessage.success('手机号修改成功，请重新登录')
    store.logout()
    await router.push('/login')
  } else {
    ElMessage.error('修改失败，请检查信息')
  }
}
</script>