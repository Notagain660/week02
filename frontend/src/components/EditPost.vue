<template>
  <div>
    <h2>编辑帖子</h2>
    <div v-if="loading">加载中...</div>
    <form v-else @submit.prevent="submit">
      <div>
        <label>类型</label>
        <select v-model="form.type" disabled>
          <option :value="0">丢失</option>
          <option :value="1">拾取</option>
        </select>
      </div>
      <div>
        <label>物品名称</label>
        <input v-model="form.itemName" required />
      </div>
      <div>
        <label>地点</label>
        <input v-model="form.itemPlace" required />
      </div>
      <div>
        <label>时间</label>
        <input type="datetime-local" v-model="form.itemTime" required />
      </div>
      <div>
        <label>描述</label>
        <textarea v-model="form.userDescription"></textarea>
      </div>
      <div>
        <label>联系方式</label>
        <input v-model="form.contact" />
      </div>
      <div>
        <label>可见范围</label>
        <select v-model="form.visible">
          <option :value="0">所有人可见</option>
          <option :value="1">仅自己可见</option>
          <option :value="2">好友可见</option>
        </select>
      </div>
      <div>
        <label>图片</label>
        <input type="file" @change="handleImageUpload" accept="image/*" />
        <img v-if="imagePreview" :src="imagePreview" style="width: 100px; margin-top: 5px;" alt=""/>
      </div>
      <button type="submit">保存修改</button>
      <DeletePostButton :post-id="postId" />
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail, updatePost, uploadPostImage } from '@/api/user'
import DeletePostButton from "@/components/DeletePostButton.vue";

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

const loadPost = async () => {
  loading.value = true
  try {
    const res = await getPostDetail(postId)
    if (res.code === 200) {
      Object.assign(form, res.data)
      if (form.itemPhoto) imagePreview.value = form.itemPhoto
      // 处理时间格式：后端返回的是字符串如 "2026-04-17T12:00:00"，datetime-local 需要 "YYYY-MM-DDThh:mm"
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