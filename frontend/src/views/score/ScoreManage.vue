<template>
  <div class="score-manage">
    <el-card>
      <template #header>成绩管理</template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="学生姓名" width="120" />
        <el-table-column prop="paperTitle" label="试卷名称" show-overflow-tooltip />
        <el-table-column prop="score" label="总分" width="100">
          <template #default="{ row }">
            <el-tag :type="scoreType(row.score)">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="objectiveScore" label="客观题" width="100" />
        <el-table-column prop="subjectiveScore" label="主观题" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="交卷时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="viewDialogVisible" title="成绩详情" width="700px">
      <div v-if="viewData" class="score-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学生姓名">{{ viewData.userName }}</el-descriptions-item>
          <el-descriptions-item label="试卷名称">{{ viewData.paperTitle }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ viewData.score }}分</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(viewData.status)">{{ statusLabel(viewData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="客观题得分">{{ viewData.objectiveScore }}分</el-descriptions-item>
          <el-descriptions-item label="主观题得分">{{ viewData.subjectiveScore }}分</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ viewData.startTime }}</el-descriptions-item>
          <el-descriptions-item label="交卷时间">{{ viewData.submitTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { scoreAPI } from '@/api'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const viewDialogVisible = ref(false)
const viewData = ref(null)

const statusLabel = (s) => ({ ING: '考试中', SUBMITTED: '已完成' }[s] || s)
const statusType = (s) => ({ ING: 'warning', SUBMITTED: 'success' }[s] || '')
const scoreType = (s) => s >= 90 ? 'success' : s >= 60 ? 'warning' : 'danger'

const loadData = async () => {
  loading.value = true
  try {
    const res = await scoreAPI.list({ pageNum: pageNum.value, pageSize: pageSize.value })
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
    const res = await scoreAPI.detail(row.id)
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
</style>
