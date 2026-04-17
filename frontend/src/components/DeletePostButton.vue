<template>
  <div>
    <div v-if="loading">加载中...</div>
    <form v-else @submit.prevent="submit">
      <button type="button" @click="deletePost" style="margin-left: 10px; color: red;">删除帖子</button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, updatePost, uploadPostImage, deletePost as apiDeletePost } from '@/api/user'

const route = useRoute()
const router = useRouter()
const postId = Number(route.params.postId)
const loading = ref(false)
const form = reactive({
  type: 0,
  itemName: '',
  itemPlace: '',
  itemTime: '',
  userDescription: '',
  contact: '',
  visible: 0,
  postStatus: 0,
  itemPhoto: ''
})
const imageFile = ref(null)
const imagePreview = ref('')

const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    imageFile.value = file
    imagePreview.value = URL.createObjectURL(file)
  }
}

const submit = async () => {
  if (!form.itemName || !form.itemPlace || !form.itemTime) {
    ElMessage.error('请填写完整信息')
    return
  }

  let imageUrl = form.itemPhoto
  if (imageFile.value) {
    const formData = new FormData()
    formData.append('image', imageFile.value)
    const res = await uploadPostImage(formData)
    if (res.code === 200) {
      imageUrl = res.data
    } else {
      ElMessage.error('图片上传失败')
      return
    }
  }

  const postData = {
    ...form,
    itemPhoto: imageUrl,
    aiDescription: form.aiDescription || ''
  }
  postData.postId = postId

  try {
    const res = await updatePost(postData)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      await router.push(`/post/${postId}`)
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

const deletePost = async () => {
  await ElMessageBox.confirm('确定要删除此帖子吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
  try {
    const res = await apiDeletePost(postId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await router.push('/posts')   // 删除后跳转到帖子列表页
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
}

const loadPost = async () => {
  loading.value = true
  try {
    const res = await getPostDetail(postId)
    if (res.code === 200) {
      Object.assign(form, res.data)
      if (form.itemPhoto) imagePreview.value = form.itemPhoto
      if (form.itemTime && typeof form.itemTime === 'string') {
        form.itemTime = form.itemTime.slice(0, 16)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载帖子失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadPost)
</script>