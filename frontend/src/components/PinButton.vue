<template>
  <button v-if="isAdmin" @click="togglePin" :disabled="loading" class="pin-btn">
    {{ loading ? '处理中...' : (pinStatus ? '取消置顶' : '置顶') }}
  </button>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pinPost, releasePin } from '@/api/user.js'
import { useStore } from '@/store'

const props = defineProps({
  postId: {
    type: Number,
    required: true
  },
  pinStatus: {
    type: Boolean,
    required: true
  }
})
const emit = defineEmits(['success'])

const store = useStore()
const isAdmin = computed(() => store.currentUser?.role === 'ADMIN')

const loading = ref(false)
const pinStatus = ref(props.pinStatus)

const togglePin = async () => {
  const action = pinStatus.value ? '取消置顶' : '置顶'
  try {
    await ElMessageBox.confirm(`确认要${action}该帖子吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    loading.value = true
    let res
    if (pinStatus.value) {
      res = await releasePin(props.postId)
    } else {
      res = await pinPost(props.postId)
    }
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      pinStatus.value = !pinStatus.value
      emit('success')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('请求出错')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.pin-btn {
  margin-left: 10px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
}
</style>