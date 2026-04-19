<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(stat, index) in stats" :key="index">
        <div class="stat-card" :style="{ background: stat.color }">
          <div class="stat-icon">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <span>考试趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="rank-card">
          <template #header>
            <span>成绩排行榜</span>
          </template>
          <div class="rank-list">
            <div v-for="(item, index) in ranking" :key="index" class="rank-item">
              <span class="rank-num" :class="{ top3: index < 3 }">{{ index + 1 }}</span>
              <span class="rank-name">{{ item.realName }}</span>
              <span class="rank-score">{{ item.totalScore }}分</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row" v-if="isStudent">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>可参加的考试</span>
              <el-button type="primary" text @click="$router.push('/examHistory')">查看全部</el-button>
            </div>
          </template>
          <div class="exam-list">
            <div v-for="paper in availablePapers" :key="paper.id" class="exam-item">
              <div class="exam-info">
                <h3>{{ paper.title }}</h3>
                <p>
                  <el-tag size="small">{{ paper.subject }}</el-tag>
                  <el-tag size="small" type="info" style="margin-left: 8px">{{ paper.gradeLevel }}</el-tag>
                  <span style="margin-left: 16px; color: #999">
                    <el-icon><Clock /></el-icon> {{ paper.duration }}分钟
                  </span>
                  <span style="margin-left: 16px; color: #999">
                    <el-icon><Files /></el-icon> 满分{{ paper.totalScore }}分
                  </span>
                </p>
              </div>
              <el-button type="primary" @click="startExam(paper.id)">开始考试</el-button>
            </div>
            <el-empty v-if="availablePapers.length === 0" description="暂无进行中的考试" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { useUserStore } from '@/store/user'
import { statisticsAPI, paperAPI } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const isStudent = computed(() => userStore.isStudent)

const stats = ref([
  { label: '用户总数', value: 0, icon: 'User', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '题目总数', value: 0, icon: 'Document', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '试卷总数', value: 0, icon: 'Tickets', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '考试次数', value: 0, icon: 'Reading', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const trendChartRef = ref()
let trendChart = null

const ranking = ref([])

const availablePapers = ref([])

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  const option = {
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
  }
  trendChart.setOption(option)
}

const loadOverview = async () => {
  try {
    const res = await statisticsAPI.overview()
    const data = res.data
    stats.value[0].value = data.userCount || 0
    stats.value[1].value = data.questionCount || 0
    stats.value[2].value = data.paperCount || 0
    stats.value[3].value = data.examCount || 0
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
  } catch (e) {
    console.error('加载趋势失败', e)
  }
}

const loadRanking = async () => {
  try {
    const res = await statisticsAPI.ranking({ limit: 10 })
    ranking.value = res.data || []
  } catch (e) {
    console.error('加载排行失败', e)
  }
}

const loadAvailablePapers = async () => {
  try {
    const res = await paperAPI.available()
    availablePapers.value = res.data || []
  } catch (e) {
    console.error('加载考试失败', e)
  }
}

const startExam = (paperId) => {
  router.push(`/exam/${paperId}`)
}

onMounted(async () => {
  await userStore.getCurrentUser()
  loadOverview()
  initTrendChart()
  loadRanking()
  if (isStudent.value) {
    loadAvailablePapers()
  }
})

watch(trendChartRef, () => {
  if (trendChartRef.value) {
    initTrendChart()
    loadTrend()
  }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}
.stat-row {
  margin-bottom: 20px;
}
.stat-card {
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.stat-icon {
  font-size: 48px;
  opacity: 0.8;
  margin-right: 20px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
}
.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 4px;
}
.content-row {
  margin-bottom: 20px;
}
.rank-list {
  max-height: 300px;
  overflow-y: auto;
}
.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
.rank-item:last-child {
  border-bottom: none;
}
.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #ddd;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-right: 12px;
}
.rank-num.top3 {
  background: #f56c6c;
}
.rank-name {
  flex: 1;
  font-size: 14px;
}
.rank-score {
  color: #409eff;
  font-weight: bold;
}
.exam-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.exam-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}
.exam-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
}
.exam-info p {
  margin: 0;
  color: #666;
  font-size: 13px;
}
</style>
