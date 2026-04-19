<template>
  <div class="paper-manage">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>试卷管理</span>
          <el-button type="primary" @click="$router.push('/paper/create')">创建试卷</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="科目">
          <el-select v-model="searchForm.subject" placeholder="请选择科目" clearable>
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="历史" value="历史" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="searchForm.gradeLevel" placeholder="请选择年级" clearable>
            <el-option label="小学" value="小学" />
            <el-option label="初中" value="初中" />
            <el-option label="高中" value="高中" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="试卷标题" show-overflow-tooltip />
        <el-table-column prop="subject" label="科目" width="80" />
        <el-table-column prop="gradeLevel" label="年级" width="80" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="duration" label="时长(分钟)" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'DRAFT'" type="success" text size="small" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 'PUBLISHED'" type="warning" text size="small" @click="handleClose(row)">关闭</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="viewDialogVisible" title="试卷预览" width="700px">
      <div v-if="viewData" class="paper-preview">
        <h2>{{ viewData.title }}</h2>
        <div class="paper-info">
          <span>科目：{{ viewData.subject }}</span>
          <span>年级：{{ viewData.gradeLevel }}</span>
          <span>总分：{{ viewData.totalScore }}</span>
          <span>时长：{{ viewData.duration }}分钟</span>
        </div>
        <div class="question-list">
          <div v-for="(q, index) in viewData.questions" :key="q.id" class="question-item">
            <div class="q-header">
              <span class="q-num">{{ index + 1 }}.</span>
              <span class="q-content">{{ q.content }}</span>
              <span class="q-score">[{{ q.score }}分]</span>
            </div>
            <div v-if="q.options && q.options.length" class="q-options">
              <div v-for="opt in q.options" :key="opt.id" class="option-item">
                {{ opt.optionLabel }}. {{ opt.optionContent }}
              </div>
            </div>
            <div class="q-answer">
              <strong>正确答案：</strong>{{ q.answer }}
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { paperAPI } from '@/api'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  subject: '',
  gradeLevel: '',
  status: ''
})

const viewDialogVisible = ref(false)
const viewData = ref(null)

const statusLabel = (s) => ({ DRAFT: '草稿', PUBLISHED: '已发布', CLOSED: '已关闭' }[s] || s)
const statusType = (s) => ({ DRAFT: 'info', PUBLISHED: 'success', CLOSED: 'warning' }[s] || '')

const loadData = async () => {
  loading.value = true
  try {
    const res = await paperAPI.list({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.subject = ''
  searchForm.gradeLevel = ''
  searchForm.status = ''
  pageNum.value = 1
  loadData()
}

const handleView = async (row) => {
  try {
    const res = await paperAPI.detail(row.id)
    viewData.value = res.data
    viewDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

const handlePublish = async (row) => {
  try {
    await paperAPI.publish(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch (e) {
    ElMessage.error('发布失败')
  }
}

const handleClose = async (row) => {
  try {
    await paperAPI.close(row.id)
    ElMessage.success('关闭成功')
    loadData()
  } catch (e) {
    ElMessage.error('关闭失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该试卷吗？', '提示', { type: 'warning' })
  try {
    await paperAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.paper-preview h2 { text-align: center; margin-bottom: 20px; }
.paper-info { display: flex; gap: 20px; justify-content: center; margin-bottom: 20px; color: #666; }
.question-list {
  max-height: 400px;
  overflow-y: auto;
  &::-webkit-scrollbar{
    display:none;
  }
}
.question-item { padding: 16px; border-bottom: 1px solid #eee; }
.q-header { display: flex; gap: 8px; }
.q-num { font-weight: bold; }
.q-content { flex: 1; }
.q-score { color: #409eff; }
.q-options { margin-top: 8px; padding-left: 20px; color: #666; }
.option-item { margin-bottom: 4px; }
.q-answer { margin-top: 8px; color: #67c23a; }
:deep(.el-form-item__content) {
  min-width:180px
}
</style>
