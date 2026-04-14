<template>
  <el-card>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="basic">
        <el-form>
          <el-form-item label="昵称">
            <el-input v-model="nickname" />
          </el-form-item>
          <el-form-item label="头像">
            <el-upload action="#" :http-request="uploadAvatar" :show-file-list="false">
              <el-button>上传新头像</el-button>
            </el-upload>
          </el-form-item>
          <el-button type="primary" @click="saveBasic">保存</el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="安全信息" name="security">
        <el-form>
          <el-form-item label="修改密码">
            <el-input type="password" v-model="pwd.old" placeholder="旧密码" />
            <el-input type="password" v-model="pwd.new" placeholder="新密码" />
            <el-input type="password" v-model="pwd.confirm" placeholder="确认新密码" />
            <el-button @click="changePassword">修改密码</el-button>
          </el-form-item>
          <el-form-item label="修改手机号">
            <el-input v-model="phone.new" placeholder="新手机号" />
            <el-button @click="changePhone">修改</el-button>
          </el-form-item>
          <el-form-item label="修改邮箱">
            <el-input v-model="email.new" placeholder="新邮箱" />
            <el-button @click="changeEmail">修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateName, updateAvatar, updatePassword, updatePhone, updateEmail } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const nickname = ref(userStore.userInfo?.userName || '')
const activeTab = ref('basic')
const pwd = ref({ old: '', new: '', confirm: '' })
const phone = ref({ new: '' })
const email = ref({ new: '' })

const uploadAvatar = async (options) => {
  const fd = new FormData()
  fd.append('image', options.file)
  const res = await updateAvatar(fd)
  if (res.code === 200) {
    ElMessage.success('头像更新成功')
    userStore.fetchUserInfo()
  }
}
const saveBasic = async () => {
  const res = await updateName(nickname.value)
  if (res.code === 200) {
    ElMessage.success('昵称已更新')
    userStore.fetchUserInfo()
  }
}
const changePassword = async () => {
  if (pwd.value.new !== pwd.value.confirm) return ElMessage.error('新密码不一致')
  const res = await updatePassword({
    str: pwd.value.new,
    passerword: pwd.value.old,
    phoner: userStore.userInfo?.userPhone || '',
    emailer: userStore.userInfo?.userEmail || '',
  })
  if (res.code === 200) ElMessage.success('密码已修改')
}
const changePhone = async () => {
  const res = await updatePhone({
    str: phone.value.new,
    passerword: pwd.value.old,
    phoner: userStore.userInfo?.userPhone || '',
    emailer: userStore.userInfo?.userEmail || '',
  })
  if (res.code === 200) ElMessage.success('手机号已修改')
}
const changeEmail = async () => {
  const res = await updateEmail({
    str: email.value.new,
    passerword: pwd.value.old,
    phoner: userStore.userInfo?.userPhone || '',
    emailer: userStore.userInfo?.userEmail || '',
  })
  if (res.code === 200) ElMessage.success('邮箱已修改')
}
</script>