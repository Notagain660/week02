<template>
  <div>
    <BlockButton @success="fetchReports" />
    <h2>举报列表</h2>
    <div v-if="loading">加载中...</div>
    <div v-else-if="reports.length === 0">暂无举报</div>
    <table v-else border="1" cellpadding="5" style="border-collapse: collapse; width: 100%;">
      <thead>
      <tr>
        <th>举报ID</th>
        <th>举报人ID</th>
        <th>类型</th>
        <th>内容ID</th>
        <th>理由</th>
        <th>状态</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="r in reports" :key="r.batchre">
        <td>{{ r.batchre }}</td>
        <td>{{ r.reporter }}</td>
          <td>{{ r.reportType === 'USER' ? '用户' : r.reportType === 'POST' ? '帖子' : '评论' }}</td>
          <td>{{ r.contentId }}</td>
            <td>{{ r.reason }}</td>
              <td>{{ r.status === 'NOT' ? '未处理' : r.status === 'ING' ? '处理中' : '已处理' }}</td>
               <td><DealReport :report-id="r.batchre" @success="fetchReports" /></td>
      </tr>
      </tbody>
    </table>
    <ReportForm />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getReportList } from '@/api/user.js'
import ReportForm from "@/components/ReportForm.vue";
import DealReport from '@/components/DealReport.vue'
import BlockButton from "@/components/BlockButton.vue";

const reports = ref([])
const loading = ref(false)

const fetchReports = async () => {
  loading.value = true
  try {
    const res = await getReportList()
    if (res.code === 200) {
      reports.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchReports)
</script>