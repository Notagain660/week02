<template>
  <div>
    <button v-if="!showForm" @click="showForm = true">举报</button>
    <div v-else>
      <h3>举报</h3>
      <form @submit.prevent="submit">
        <div>
          <label>被举报用户ID：</label>
          <input v-model="form.reportee" type="number" required />
        </div>
        <div>
          <label>举报类型：</label>
          <select v-model="form.reportType">
            <option :value="0">用户</option>
            <option :value="1">帖子</option>
            <option :value="2">评论</option>
          </select>
        </div>
        <div>
          <label>内容ID：</label>
          <input v-model="form.contentId" type="number" required />
        </div>
        <div>
          <label>理由：</label>
          <textarea v-model="form.reason" required></textarea>
        </div>
        <button type="submit">提交举报</button>
        <button type="button" @click="showForm = false">取消</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addReport } from '@/api/user.js'

const showForm = ref(false)
const form = reactive({
  reportee: '',
  reportType: 0,
  contentId: '',
  reason: ''
})

const submit = async () => {
  if (!form.reportee || !form.contentId || !form.reason) {
    ElMessage.error('请填写完整信息')
    return
  }
  try {
    const res = await addReport({
      reportee: Number(form.reportee),
      reportType: Number(form.reportType),
      contentId: Number(form.contentId),
      reason: form.reason
    })
    if (res.code === 200) {
      ElMessage.success('举报成功')
      form.reportee = ''
      form.contentId = ''
      form.reason = ''
      form.reportType = 0
      showForm.value = false
    } else {
      ElMessage.error(res.message || '举报失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求出错')
  }
}
</script>