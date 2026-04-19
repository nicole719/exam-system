<template>
  <div class="wrong-book">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>错题本</span>
          <el-tag type="danger">共 {{ total }} 道错题</el-tag>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="100" />
        <el-table-column prop="wrongCount" label="错误次数" width="100">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.wrongCount }}次</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answer" label="正确答案" width="120" />
        <el-table-column prop="lastPracticeTime" label="最近练习" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handlePractice(row)">练习</el-button>
            <el-button type="danger" text size="small" @click="handleRemove(row)">移除</el-button>
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

    <el-dialog v-model="practiceDialogVisible" title="错题练习" width="700px" :close-on-click-modal="false">
      <div v-if="practiceQuestion" class="practice-card">
        <div class="q-meta">
          <el-tag size="small">{{ practiceQuestion.questionType }}</el-tag>
          <el-tag size="small" type="danger">错误{{ practiceWrong.wrongCount }}次</el-tag>
        </div>
        <div class="q-content">{{ practiceQuestion.content }}</div>

        <div v-if="practiceQuestion.questionType === '单选题'" class="q-options">
          <el-radio-group v-model="practiceAnswer">
            <el-radio v-for="opt in practiceQuestion.options" :key="opt.id" :value="opt.optionLabel">
              {{ opt.optionLabel }}. {{ opt.optionContent }}
            </el-radio>
          </el-radio-group>
        </div>
        <div v-else-if="practiceQuestion.questionType === '判断题'" class="q-options">
          <el-radio-group v-model="practiceAnswer">
            <el-radio value="true">正确</el-radio>
            <el-radio value="false">错误</el-radio>
          </el-radio-group>
        </div>
        <div v-else class="q-input">
          <el-input v-model="practiceAnswer" type="textarea" :rows="3" placeholder="请输入答案" />
        </div>

        <div v-if="showResult" class="result-panel" :class="{ correct: isCorrect, wrong: !isCorrect }">
          <div class="result-header">
            <el-icon v-if="isCorrect"><SuccessFilled /></el-icon>
            <el-icon v-else><CircleCloseFilled /></el-icon>
            {{ isCorrect ? '回答正确！' : '回答错误' }}
          </div>
          <div v-if="!isCorrect" class="result-detail">
            <div>正确答案：{{ practiceQuestion.answer }}</div>
            <div>你的答案：{{ practiceAnswer }}</div>
          </div>
          <div v-if="practiceQuestion.analysis" class="result-analysis">
            <strong>解析：</strong>{{ practiceQuestion.analysis }}
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="practiceDialogVisible = false">关闭</el-button>
        <el-button v-if="!showResult" type="primary" @click="checkAnswer">提交答案</el-button>
        <el-button v-else type="primary" @click="nextQuestion">下一题</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { wrongAPI, questionAPI } from '@/api'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const practiceDialogVisible = ref(false)
const practiceQuestion = ref(null)
const practiceWrong = ref(null)
const practiceAnswer = ref('')
const showResult = ref(false)
const isCorrect = ref(false)
let currentWrongId = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await wrongAPI.list({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handlePractice = async (row) => {
  try {
    const res = await questionAPI.detail(row.questionId)
    practiceQuestion.value = res.data
    practiceWrong.value = row
    currentWrongId = row.id
    practiceAnswer.value = ''
    showResult.value = false
    practiceDialogVisible.value = true
    await wrongAPI.practice(row.id)
  } catch (e) {
    ElMessage.error('加载题目失败')
  }
}

const checkAnswer = () => {
  if (!practiceAnswer.value) {
    ElMessage.warning('请输入答案')
    return
  }
  const correct = (practiceQuestion.value.answer || '').toUpperCase()
  const answer = (practiceAnswer.value || '').toUpperCase()
  isCorrect.value = correct === answer
  showResult.value = true

  if (isCorrect.value) {
    ElMessage.success('恭喜你答对了！')
  }
}

const nextQuestion = () => {
  practiceDialogVisible.value = false
  loadData()
}

const handleRemove = async (row) => {
  await ElMessageBox.confirm('确定从错题本中移除吗？', '提示', { type: 'warning' })
  try {
    await wrongAPI.remove(row.id)
    ElMessage.success('移除成功')
    loadData()
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.practice-card { padding: 10px; }
.q-meta { display: flex; gap: 8px; margin-bottom: 16px; }
.q-content { font-size: 16px; line-height: 1.8; margin-bottom: 20px; }
.q-options { display: flex; flex-direction: column; gap: 12px; }
.q-input { margin-top: 12px; }
.result-panel { margin-top: 20px; padding: 16px; border-radius: 8px; }
.result-panel.correct { background: #f0f9eb; border: 1px solid #e1f3d8; }
.result-panel.wrong { background: #fef0f0; border: 1px solid #fde2e2; }
.result-header { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: bold; margin-bottom: 8px; }
.correct .result-header { color: #67c23a; }
.wrong .result-header { color: #f56c6c; }
.result-detail { margin: 8px 0; color: #666; }
.result-detail div { margin: 4px 0; }
.result-analysis { margin-top: 8px; color: #666; }
</style>
