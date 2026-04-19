<template>
  <div class="question-manage">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>题库管理</span>
          <el-button type="primary" @click="handleCreate">添加题目</el-button>
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
        <el-form-item label="题型">
          <el-select v-model="searchForm.questionType" placeholder="请选择题型" clearable>
            <el-option label="单选题" value="单选题" />
            <el-option label="多选题" value="多选题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="简答题" value="简答题" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="请选择难度" clearable>
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="subject" label="科目" width="80" />
        <el-table-column prop="gradeLevel" label="年级" width="80" />
        <el-table-column prop="questionType" label="题型" width="100" />
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="difficultyType(row.difficulty)">{{ difficultyLabel(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
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
        <el-form-item label="年级" prop="gradeLevel">
          <el-select v-model="form.gradeLevel">
            <el-option label="小学" value="小学" />
            <el-option label="初中" value="初中" />
            <el-option label="高中" value="高中" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型" prop="questionType">
          <el-select v-model="form.questionType" @change="handleTypeChange">
            <el-option label="单选题" value="单选题" />
            <el-option label="多选题" value="多选题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="简答题" value="简答题" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-radio-group v-model="form.difficulty">
            <el-radio :label="1">简单</el-radio>
            <el-radio :label="2">中等</el-radio>
            <el-radio :label="3">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="form.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>

        <el-form-item v-if="showOptions" label="选项">
          <div v-for="(opt, index) in form.options" :key="index" class="option-item">
            <el-input v-model="opt.optionLabel" style="width: 60px" placeholder="标签" />
            <el-input v-model="opt.optionContent" style="flex: 1" placeholder="选项内容" />
            <el-checkbox v-model="opt.isCorrect" style="margin-left: 8px">正确答案</el-checkbox>
            <el-button type="danger" text @click="removeOption(index)">删除</el-button>
          </div>
          <el-button type="primary" text @click="addOption">添加选项</el-button>
        </el-form-item>

        <el-form-item v-if="!showOptions" label="正确答案" prop="answer">
          <el-input v-model="form.answer" placeholder="请输入正确答案" />
        </el-form-item>

        <el-form-item label="答案解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="请输入答案解析（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { questionAPI } from '@/api'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  subject: '',
  gradeLevel: '',
  questionType: '',
  difficulty: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: null,
  subject: '语文',
  gradeLevel: '初中',
  questionType: '单选题',
  difficulty: 1,
  score: 5,
  content: '',
  answer: '',
  analysis: '',
  options: []
})

const rules = {
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  gradeLevel: [{ required: true, message: '请选择年级', trigger: 'change' }],
  questionType: [{ required: true, message: '请选择题型', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }]
}

const showOptions = computed(() => ['单选题', '多选题'].includes(form.questionType))

const difficultyLabel = (d) => ({ 1: '简单', 2: '中等', 3: '困难' }[d] || '')
const difficultyType = (d) => ({ 1: 'success', 2: 'warning', 3: 'danger' }[d] || '')

const loadData = async () => {
  loading.value = true
  try {
    const res = await questionAPI.list({
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
  searchForm.questionType = ''
  searchForm.difficulty = null
  pageNum.value = 1
  loadData()
}

const addOption = () => {
  const labels = ['A', 'B', 'C', 'D', 'E', 'F']
  form.options.push({
    optionLabel: labels[form.options.length] || '',
    optionContent: '',
    isCorrect: false
  })
}

const removeOption = (index) => {
  form.options.splice(index, 1)
}

const handleTypeChange = () => {
  if (form.questionType === '单选题' || form.questionType === '多选题') {
    if (form.options.length === 0) {
      addOption()
      addOption()
      addOption()
      addOption()
    }
  }
}

const handleCreate = () => {
  dialogTitle.value = '添加题目'
  Object.assign(form, {
    id: null, subject: '语文', gradeLevel: '初中', questionType: '单选题',
    difficulty: 1, score: 5, content: '', answer: '', analysis: '', options: []
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑题目'
  try {
    const res = await questionAPI.detail(row.id)
    Object.assign(form, res.data)
    if (!form.options) form.options = []
    dialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (showOptions.value) {
      const correctOptions = form.options.filter(o => o.isCorrect)
      if (correctOptions.length === 0) {
        ElMessage.error('请至少选择一个正确答案')
        return
      }
      form.answer = correctOptions.map(o => o.optionLabel).join(',')
    }
    if (form.id) {
      await questionAPI.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await questionAPI.create(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该题目吗？', '提示', { type: 'warning' })
  try {
    await questionAPI.delete(row.id)
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
.option-item { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
:deep(.el-form-item__content) {
  min-width:180px
}
</style>
