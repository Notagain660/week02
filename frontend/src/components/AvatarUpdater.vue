<template>
  <div>
    <button @click="triggerFileInput">更换头像</button>
    <input
        type="file"
        ref="fileInput"
        style="display: none"
        accept="image/jpeg,image/png,image/gif"
        @change="handleFileChange"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from '@/store'

const store = useStore()
const fileInput = ref(null)
const avatarUrl = ref(store.currentUser?.userAvatar || '')

const triggerFileInput = () => fileInput.value.click()

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  try {
    const success = await store.updateAvatar(file)
    if (success) {
      avatarUrl.value = store.currentUser.userAvatar
      alert('头像更新成功')
    } else {
      alert('头像更新失败')
    }
  } catch (error) {
    console.error(error)
    alert('请求出错')
  }
  event.target.value = ''
}
</script>