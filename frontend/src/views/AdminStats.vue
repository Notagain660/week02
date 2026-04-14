<template>
  <div>
    <el-card>
      <div class="stat-cards">
        <el-statistic title="总帖数" :value="totalPosts" />
        <el-statistic title="已找回物品数" :value="totalFound" />
        <el-statistic title="活跃用户数" :value="activeCount" />
      </div>
      <el-form inline>
        <el-form-item label="活跃统计时间">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" />
          <el-button @click="fetchActive">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card style="margin-top: 20px">
      <el-radio-group v-model="chartType" @change="fetchChartData">
        <el-radio label="item">物品丢失统计</el-radio>
        <el-radio label="place">地点丢失统计</el-radio>
      </el-radio-group>
      <div ref="chartDom" style="height: 400px; margin-top: 20px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getTotalPosts, getTotalFound, getActiveUsers, getItemStatistics, getPlaceStatistics } from '@/api/admin'

const totalPosts = ref(0)
const totalFound = ref(0)
const activeCount = ref(0)
const dateRange = ref([])
const chartType = ref('item')
const chartDom = ref(null)
let chart = null

const fetchActive = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return
  const [start, end] = dateRange.value
  const res = await getActiveUsers(start.toISOString().slice(0,10), end.toISOString().slice(0,10))
  if (res.code === 200) activeCount.value = res.data
}
const fetchChartData = async () => {
  let res
  if (chartType.value === 'item') res = await getItemStatistics()
  else res = await getPlaceStatistics()
  if (res.code === 200) {
    const data = JSON.parse(res.data.OriginData)
    const names = data.map(d => d.itemname || d.itemplace)
    const counts = data.map(d => d.count)
    if (chart) chart.dispose()
    chart = echarts.init(chartDom.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        data: names.map((n, i) => ({ name: n, value: counts[i] })),
      }],
    })
  }
}
onMounted(async () => {
  const [postsRes, foundRes] = await Promise.all([getTotalPosts(), getTotalFound()])
  if (postsRes.code === 200) totalPosts.value = postsRes.data
  if (foundRes.code === 200) totalFound.value = foundRes.data
  await fetchChartData()
})
</script>

<style scoped>
.stat-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}
</style>