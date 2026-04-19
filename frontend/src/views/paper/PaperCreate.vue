<template>
  <div class="paper-create">
    <el-card>
      <template #header>
        <el-button text @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <span style="margin-left: 16px">创建试卷</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="paper-form">
        <el-form-item label="试卷标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入试卷标题" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="科目" prop="subject">
              <el-select v-model="form.subject">
                <el-option label="语文" value="语文" />
                <el-option label="数学" value="数学" />
                <el-option label="英语" value="英语" />
                <el-option label="物理" value="物理" />
                <el-option label="化学" value="化学" />
                <el-option label="历史" value="历史" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年级" prop="gradeLevel">
              <el-select v-model="form.gradeLevel">
                <el-option label="小学" value="小学" />
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时长(分钟)" prop="duration">
              <el-input-number v-model="form.duration" :min="1" :max="300" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="试卷描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入试卷描述（可选）" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>选择题目</span>
        <div style="float: right">
          <el-button type="primary" text @click="showQuestionDialog = true">手动选题</el-button>
          <el-button type="success" text @click="showAutoDialog = true">自动组卷</el-button>
        </div>
      </template>

      <el-table :data="selectedQuestions" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="100" />
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ difficultyLabel(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="removeQuestion(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="total-score">
        总分：<span class="score-value">{{ totalScore }}</span> 分，
        共 <span class="score-value">{{ selectedQuestions.length }}</span> 题
      </div>
    </el-card>

    <div class="action-bar">
      <el-button @click="$router.back()">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存试卷</el-button>
    </div>

    <el-dialog v-model="showQuestionDialog" title="选择题目" width="900px">
      <el-form :inline="true" :model="questionSearch" class="search-form">
        <el-form-item label="科目">
          <el-select v-model="questionSearch.subject" placeholder="请选择科目" clearable>
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="questionSearch.questionType" placeholder="请选择题型" clearable>
            <el-option label="单选题" value="单选题" />
            <el-option label="多选题" value="多选题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="简答题" value="简答题" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadQuestions">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="questionList" @selection-change="handleSelectionChange" stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="100" />
        <el-table-column prop="score" label="分值" width="80" />
      </el-table>

      <div style="margin-top: 16px; text-align: right">
        <el-button @click="showQuestionDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSelectQuestions">确认选择</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="showAutoDialog" title="自动组卷" width="500px">
      <el-form :model="autoForm" label-width="100px">
        <el-form-item label="单选题数量">
          <el-input-number v-model="autoForm.singleCount" :min="0" :max="50" />
        </el-form-item>
        <el-form-item label="多选题数量">
          <el-input-number v-model="autoForm.multiCount" :min="0" :max="20" />
        </el-form-item>
        <el-form-item label="判断题数量">
          <el-input-number v-model="autoForm.judgeCount" :min="0" :max="30" />
        </el-form-item>
        <el-form-item label="填空题数量">
          <el-input-number v-model="autoForm.fillCount" :min="0" :max="20" />
        </el-form-item>
        <el-form-item label="简答题数量">
          <el-input-number v-model="autoForm.essayCount" :min="0" :max="10" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="autoForm.difficulty" placeholder="请选择难度" clearable>
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAutoDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAutoGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { paperAPI, questionAPI } from '@/api'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const loading = ref(false)

const form = reactive({
  title: '',
  subject: '语文',
  gradeLevel: '初中',
  duration: 60,
  startTime: null,
  endTime: null,
  description: '',
  questionIds: []
})

const rules = {
  title: [{ required: true, message: '请输入试卷标题', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  gradeLevel: [{ required: true, message: '请选择年级', trigger: 'change' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }]
}

const selectedQuestions = ref([])
const totalScore = computed(() => selectedQuestions.value.reduce((sum, q) => sum + (q.score || 5), 0))

const difficultyLabel = (d) => ({ 1: '简单', 2: '中等', 3: '困难' }[d] || '')

const showQuestionDialog = ref(false)
const questionSearch = reactive({ subject: '', questionType: '' })
const questionList = ref([])
const selectedRows = ref([])

const showAutoDialog = ref(false)
const autoForm = reactive({
  singleCount: 5, multiCount: 3, judgeCount: 5, fillCount: 3, essayCount: 2, difficulty: null
})

const loadQuestions = async () => {
  loading.value = true
  try {
    const res = await questionAPI.list({
      pageNum: 1, pageSize: 100,
      subject: questionSearch.subject || undefined,
      questionType: questionSearch.questionType || undefined,
      gradeLevel: form.gradeLevel
    })
    questionList.value = res.data.records || []
  } catch (e) {
    ElMessage.error('加载题目失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const confirmSelectQuestions = () => {
  selectedRows.value.forEach(q => {
    if (!selectedQuestions.value.find(s => s.id === q.id)) {
      selectedQuestions.value.push(q)
    }
  })
  ElMessage.success(`已添加 ${selectedRows.value.length} 道题目`)
  showQuestionDialog.value = false
}

const removeQuestion = (row) => {
  selectedQuestions.value = selectedQuestions.value.filter(q => q.id !== row.id)
}

const handleAutoGenerate = async () => {
  const types = [
    { type: '单选题', count: autoForm.singleCount },
    { type: '多选题', count: autoForm.multiCount },
    { type: '判断题', count: autoForm.judgeCount },
    { type: '填空题', count: autoForm.fillCount },
    { type: '简答题', count: autoForm.essayCount }
  ]

  for (const t of types) {
    if (t.count > 0) {
      try {
        const res = await questionAPI.autoGenerate({
          subject: form.subject,
          gradeLevel: form.gradeLevel,
          questionType: t.type,
          count: t.count,
          difficulty: autoForm.difficulty
        })
        res.data.forEach(q => {
          if (!selectedQuestions.value.find(s => s.id === q.id)) {
            selectedQuestions.value.push(q)
          }
        })
      } catch (e) {
        console.error(`加载${t.type}失败`, e)
      }
    }
  }
  ElMessage.success('自动组卷完成')
  showAutoDialog.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }

  submitting.value = true
  try {
    await paperAPI.create({
      ...form,
      questionIds: selectedQuestions.value.map(q => q.id)
    })
    ElMessage.success('试卷创建成功')
    router.push('/paper')
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.paper-form { max-width: 800px; }
.total-score { margin-top: 16px; text-align: right; font-size: 16px; color: #666; }
.score-value { color: #409eff; font-weight: bold; font-size: 20px; }
.action-bar { margin-top: 20px; text-align: center; }
.search-form { margin-bottom: 16px; }
:deep(.el-form-item__content) {
  min-width:180px
}
</style>
