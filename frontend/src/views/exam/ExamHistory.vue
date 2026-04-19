<template>
  <div class="exam-history">
    <el-card>
      <template #header>考试记录</template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="paperTitle" label="试卷名称" show-overflow-tooltip />
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <span :class="{ 'high-score': row.score >= 90, 'low-score': row.score < 60 }">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="objectiveScore" label="客观题得分" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="submitTime" label="交卷时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleView(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="viewDialogVisible" title="成绩详情" width="700px">
      <div v-if="viewData" class="score-detail">
        <div class="detail-header">
          <h3>{{ viewData.paperTitle }}</h3>
          <div class="score-overview">
            <div class="score-main">
              <span class="score-num">{{ viewData.score }}</span>
              <span class="score-unit">分</span>
            </div>
            <div class="score-breakdown">
              <div>客观题：{{ viewData.objectiveScore }}分</div>
              <div>主观题：{{ viewData.subjectiveScore }}分</div>
            </div>
          </div>
        </div>
        <div class="detail-stats">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ viewData.totalQuestions || 0 }}</div>
                <div class="stat-label">总题数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item correct">
                <div class="stat-value">{{ viewData.correctCount || 0 }}</div>
                <div class="stat-label">正确数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item wrong">
                <div class="stat-value">{{ viewData.wrongCount || 0 }}</div>
                <div class="stat-label">错误数</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { examAPI } from '@/api'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const viewDialogVisible = ref(false)
const viewData = ref(null)

const statusLabel = (s) => ({ ING: '考试中', SUBMITTED: '已完成' }[s] || s)
const statusType = (s) => ({ ING: 'warning', SUBMITTED: 'success' }[s] || '')

const loadData = async () => {
  loading.value = true
  try {
    const res = await examAPI.history({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  try {
    const res = await examAPI.detail(row.id)
    viewData.value = res.data
    viewDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.high-score { color: #67c23a; font-weight: bold; }
.low-score { color: #f56c6c; font-weight: bold; }
.score-detail { padding: 10px; }
.detail-header { text-align: center; margin-bottom: 20px; }
.detail-header h3 { margin: 0 0 16px; }
.score-overview { display: flex; justify-content: center; align-items: center; gap: 40px; }
.score-main { display: flex; align-items: baseline; }
.score-num { font-size: 48px; color: #409eff; font-weight: bold; }
.score-unit { font-size: 18px; color: #999; margin-left: 4px; }
.score-breakdown { color: #666; }
.detail-stats { margin-top: 20px; }
.stat-item { text-align: center; padding: 20px; background: #f9f9f9; border-radius: 8px; }
.stat-item.correct { background: #e6f7e6; }
.stat-item.wrong { background: #fef0f0; }
.stat-value { font-size: 28px; font-weight: bold; color: #333; }
.stat-label { font-size: 14px; color: #999; margin-top: 4px; }
</style>
