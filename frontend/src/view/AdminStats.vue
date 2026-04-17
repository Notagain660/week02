<template>
  <div class="admin-stats">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-title">总帖数</div>
        <div class="stat-value">{{ totalPosts }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-title">已找回物品数</div>
        <div class="stat-value">{{ totalFound }}</div>
      </div>
    </div>
    <ActiveUserStats />
    <AdminDeleteButton/>
    <AdminViewUser />
    <!-- 物品统计 -->
    <div class="stat-section">
      <h3>丢失物品统计（饼图）</h3>
      <div ref="itemChartRef" style="width: 100%; height: 400px;"></div>
      <div class="ai-summary">{{ itemSummary || '加载中...' }}</div>
    </div>

    <!-- 地点统计 -->
    <div class="stat-section">
      <h3>丢失地点统计（饼图）</h3>
      <div ref="placeChartRef" style="width: 100%; height: 400px;"></div>
      <div class="ai-summary">{{ placeSummary || '加载中...' }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getTotalPosts, getTotalFound, getItemStatistics, getPlaceStatistics } from '@/api/user.js'
import ActiveUserStats from "@/components/ActiveUserStats.vue";
import AdminDeleteButton from "@/components/AdminDeleteButton.vue";
import AdminViewUser from "@/components/AdminViewUser.vue";

const totalPosts = ref(0)
const totalFound = ref(0)
const itemSummary = ref('')
const placeSummary = ref('')
const itemChartRef = ref(null)
const placeChartRef = ref(null)

let itemChart = null
let placeChart = null

// 解析 OriginData 字符串为数组
const parseOriginData = (originDataStr) => {
  try {
    const data = JSON.parse(originDataStr)
    if (Array.isArray(data)) {
      return data
    }
    return []
  } catch (e) {
    console.error('解析统计数据失败', e)
    return []
  }
}

// 绘制饼图
const renderChart = (chartDom, data, labelKey, valueKey) => {
  if (!chartDom) return
  const chart = echarts.init(chartDom)
  const chartData = data.map(item => ({
    name: item[labelKey],
    value: item[valueKey]
  }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: chartData,
      emphasis: { scale: true },
      label: { show: true, formatter: '{b}: {d}%' }
    }]
  })
  return chart
}

const loadData = async () => {
  try {
    const [postsRes, foundRes, itemRes, placeRes] = await Promise.all([
      getTotalPosts(),
      getTotalFound(),
      getItemStatistics(),
      getPlaceStatistics()
    ])
    if (postsRes.code === 200) totalPosts.value = postsRes.data
    if (foundRes.code === 200) totalFound.value = foundRes.data

    // 处理物品统计
    if (itemRes.code === 200) {
      itemSummary.value = itemRes.data.AISummary || '暂无总结'
      const originData = parseOriginData(itemRes.data.OriginData)
      if (originData.length && itemChartRef.value) {
        await nextTick()
        if (itemChart) itemChart.dispose()
        itemChart = renderChart(itemChartRef.value, originData, 'itemname', 'count')
      }
    }

    // 处理地点统计
    if (placeRes.code === 200) {
      placeSummary.value = placeRes.data.AISummary || '暂无总结'
      const originData = parseOriginData(placeRes.data.OriginData)
      if (originData.length && placeChartRef.value) {
        await nextTick()
        if (placeChart) placeChart.dispose()
        placeChart = renderChart(placeChartRef.value, originData, 'itemplace', 'count')
      }
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-stats {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.stat-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}
.stat-card {
  flex: 1;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.stat-title {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}
.stat-section {
  margin-bottom: 40px;
}
.stat-section h3 {
  margin-bottom: 15px;
}
.ai-summary {
  background: #f0f2f5;
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
  line-height: 1.6;
  color: #333;
}
</style>