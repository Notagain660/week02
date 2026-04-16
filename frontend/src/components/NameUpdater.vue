<template>
  <div>
    <!-- 点击按钮显示输入框 -->
    <button v-if="!showInput" @click="openInput">编辑昵称</button>
    <div v-else>
      <input
          v-model="newName"
          @keyup.enter="save"
          @blur="save"
          ref="inputRef"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useStore } from '@/store'

const store = useStore()
const showInput = ref(false)      // 控制输入框显示
const newName = ref('')           // 临时存储新昵称
const inputRef = ref(null)

const save = async () => {
  const trimmed = newName.value?.trim()
  if (!trimmed) {
    showInput.value = false
    return
  }
  const success = await store.updateName(trimmed)
  if (success) {
    alert('昵称修改成功')
  } else {
    alert('修改失败')
  }
  showInput.value = false
  newName.value = ''  // 清空输入框
}

// 当显示输入框时自动聚焦
const openInput = () => {
  showInput.value = true
  nextTick(() => inputRef.value?.focus())
}
</script>