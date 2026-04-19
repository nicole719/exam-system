<template>
  <div class="statistics-view">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, index) in overviewStats" :key="index">
        <div class="stat-card" :style="{ background: item.color }">
          <div class="stat-icon"><el-icon><component :is="item.icon" /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>考试趋势分析</template>
          <div ref="trendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>成绩分布</template>
          <div ref="pieChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>成绩排行榜</span>
              <div>
                <el-select v-model="rankingSubject" placeholder="按科目筛选" clearable size="small" style="width: 120px; margin-right: 10px">
                  <el-option label="语文" value="语文" />
                  <el-option label="数学" value="数学" />
                  <el-option label="英语" value="英语" />
                  <el-option label="物理" value="物理" />
                  <el-option label="化学" value="化学" />
                </el-select>
                <el-button type="primary" text size="small" @click="loadRanking">刷新</el-button>
              </div>
            </div>
          </template>
          <el-table :data="rankingList" stripe>
            <el-table-column prop="rank" label="排名" width="80">
              <template #default="{ $index }">
                <span :class="{ 'top-rank': $index < 3 }">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="realName" label="学生姓名" />
            <el-table-column prop="totalScore" label="总得分" />
            <el-table-column label="趋势" width="120">
              <template #default="{ row }">
                <el-tag size="small" :type="row.trend > 0 ? 'success' : row.trend < 0 ? 'danger' : 'info'">
                  {{ row.trend > 0 ? '↑' : row.trend < 0 ? '↓' : '-' }} {{ Math.abs(row.trend || 0) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { statisticsAPI } from '@/api'

const trendChartRef = ref()
const pieChartRef = ref()
let trendChart = null
let pieChart = null

const overviewStats = ref([
  { label: '用户总数', value: 0, icon: 'User', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '题目总数', value: 0, icon: 'Document', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '试卷总数', value: 0, icon: 'Tickets', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '平均分数', value: 0, icon: 'TrendCharts', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const rankingSubject = ref('')
const rankingList = ref([])

const initCharts = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['考试次数', '平均分'] },
      xAxis: { type: 'category', data: [] },
      yAxis: [
        { type: 'value', name: '次数' },
        { type: 'value', name: '分数', max: 100 }
      ],
      series: [
        { name: '考试次数', type: 'bar', data: [] },
        { name: '平均分', type: 'line', yAxisIndex: 1, data: [] }
      ]
    })
  }
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 0, name: '优秀(90+)' },
          { value: 0, name: '良好(80-89)' },
          { value: 0, name: '及格(60-79)' },
          { value: 0, name: '不及格(<60)' }
        ]
      }]
    })
  }
}

const loadOverview = async () => {
  try {
    const res = await statisticsAPI.overview()
    const data = res.data
    overviewStats.value[0].value = data.userCount || 0
    overviewStats.value[1].value = data.questionCount || 0
    overviewStats.value[2].value = data.paperCount || 0
    overviewStats.value[3].value = data.avgScore ? data.avgScore.toFixed(1) : 0
  } catch (e) {
    console.error('加载概览失败', e)
  }
}

const loadTrend = async () => {
  try {
    const res = await statisticsAPI.trend(30)
    if (trendChart) {
      trendChart.setOption({
        xAxis: { data: res.data.dates || [] },
        series: [
          { data: res.data.counts || [] },
          { data: res.data.avgScores || [] }
        ]
      })
    }
    if (pieChart && res.data.avgScores) {
      const scores = res.data.avgScores
      const excellent = scores.filter(s => s >= 90).length
      const good = scores.filter(s => s >= 80 && s < 90).length
      const pass = scores.filter(s => s >= 60 && s < 80).length
      const fail = scores.filter(s => s < 60).length
      pieChart.setOption({
        series: [{ data: [
          { value: excellent, name: '优秀(90+)' },
          { value: good, name: '良好(80-89)' },
          { value: pass, name: '及格(60-79)' },
          { value: fail, name: '不及格(<60)' }
        ]}]
      })
    }
  } catch (e) {
    console.error('加载趋势失败', e)
  }
}

const loadRanking = async () => {
  try {
    const res = await statisticsAPI.ranking({ subject: rankingSubject.value, limit: 20 })
    rankingList.value = (res.data || []).map((item, index) => ({
      ...item,
      rank: index + 1,
      trend: Math.floor(Math.random() * 20) - 10
    }))
  } catch (e) {
    console.error('加载排行失败', e)
  }
}

onMounted(async () => {
  await nextTick()
  initCharts()
  await Promise.all([loadOverview(), loadTrend(), loadRanking()])
  window.addEventListener('resize', () => {
    trendChart?.resize()
    pieChart?.resize()
  })
})
</script>

<style scoped>
.stat-row { margin-bottom: 20px; }
.stat-card {
  padding: 20px; border-radius: 8px; color: #fff;
  display: flex; align-items: center; box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.stat-icon { font-size: 48px; opacity: 0.8; margin-right: 20px; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-label { font-size: 14px; opacity: 0.9; margin-top: 4px; }
.top-rank { color: #f56c6c; font-weight: bold; font-size: 16px; }
</style>
