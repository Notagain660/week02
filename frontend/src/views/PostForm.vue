<template>
  <el-card>
    <h2>{{ isEdit ? '编辑帖子' : '发布帖子' }}</h2>
    <el-form :model="form" label-width="100px">
      <el-form-item label="类型">
        <el-select v-model="form.type" :disabled="isEdit">
          <el-option label="丢失" :value="0" />
          <el-option label="拾取" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="物品名称">
        <el-input v-model="form.itemName" />
      </el-form-item>
      <el-form-item label="地点">
        <el-input v-model="form.itemPlace" />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker v-model="form.itemTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.userDescription" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item label="AI描述">
        <el-button @click="generateAiDesc">生成AI描述</el-button>
        <el-input v-model="form.aiDescription" type="textarea" rows="2" />
      </el-form-item>
      <el-form-item label="图片">
        <el-upload action="#" :http-request="uploadImage" :show-file-list="false">
          <el-button>上传图片</el-button>
        </el-upload>
        <img v-if="form.itemPhoto" :src="form.itemPhoto" style="width: 100px; margin-top: 10px;" alt=""/>
      </el-form-item>
      <el-form-item label="联系方式">
        <el-input v-model="form.contact" />
      </el-form-item>
      <el-form-item label="可见范围">
        <el-radio-group v-model="form.visible">
          <el-radio :label="0">所有人可见</el-radio>
          <el-radio :label="1">仅自己可见</el-radio>
          <el-radio :label="2">好友可见</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="isEdit" label="状态">
        <el-select v-model="form.postStatus">
          <el-option label="未处理" :value="0" />
          <el-option label="已完成" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createPost, updatePost, getPostDetail, uploadPostImage } from '@/api/post'

const route = useRoute()
const router = useRouter()
const isEdit = !!route.params.postId

const form = reactive({
  type: 0,
  itemName: '',
  itemPlace: '',
  itemTime: '',
  userDescription: '',
  aiDescription: '',
  itemPhoto: '',
  contact: '',
  visible: 0,
  postStatus: 0,
})

// 提示：AI描述将在后端自动生成，无需前端调用接口
const generateAiDesc = () => {
  ElMessage.info('发布后系统会自动生成AI描述')
}

const uploadImage = async (options) => {
  const res = await uploadPostImage(options.file)
  if (res.code === 200) {
    form.itemPhoto = res.data
    ElMessage.success('图片上传成功')
  }
}

const submit = async () => {
  // 校验必填项
  if (!form.itemName || !form.itemPlace || !form.itemTime) {
    ElMessage.error('请填写完整信息')
    return
  }

  // 复制表单数据
  const postData = { ...form }

  // 时间格式转换（如果后端要求特定格式，这里已经通过日期选择器的 value-format 处理了）
  // 如果日期选择器没有设置 value-format，则需要手动转换
  if (postData.itemTime && typeof postData.itemTime === 'object') {
    const d = postData.itemTime
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')
    postData.itemTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }

  try {
    if (isEdit) {
      postData.postId = Number(route.params.postId)
      const res = await updatePost(postData)
      if (res.code === 200) {
        ElMessage.success('更新成功')
        await router.push(`/post/${postData.postId}`)
      }
    } else {
      const res = await createPost(postData)
      if (res.code === 200) {
        ElMessage.success('发布成功')
        await router.push('/')
      }
    }
  } catch (error) {
    console.error('提交失败', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 编辑模式：加载现有帖子数据
if (isEdit) {
  onMounted(async () => {
    const res = await getPostDetail(Number(route.params.postId))
    if (res.code === 200) {
      Object.assign(form, res.data)
      // 如果后端返回的时间格式是 ISO 字符串，可能需要转换以适配日期选择器
      if (form.itemTime && typeof form.itemTime === 'string') {
        // 将 "2025-04-15T12:30:00" 转换为 Date 对象
        form.itemTime = new Date(form.itemTime)
      }
    }
  })
}
</script>