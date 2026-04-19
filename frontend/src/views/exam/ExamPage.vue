<template>
  <div class="exam-page" v-if="examInfo">
    <div class="exam-header">
      <div class="exam-title">
        <h2>{{ examInfo.title }}</h2>
        <p>{{ examInfo.subject }} · {{ examInfo.gradeLevel }} · 满分{{ examInfo.totalScore }}分</p>
      </div>
      <div class="exam-timer">
        <el-icon><Clock /></el-icon>
        <span :class="{ 'time-warning': remainingTime < 300 }">{{ formatTime(remainingTime) }}</span>
      </div>
    </div>

    <div class="exam-content">
      <div class="question-area">
        <div v-for="(question, index) in examInfo.questions" :key="question.id" class="question-card"
             :class="{ active: currentIndex === index }" @click="currentIndex = index">
          <div class="q-num">{{ index + 1 }}.</div>
          <div class="q-body">
            <div class="q-meta">
              <el-tag size="small" type="info">{{ question.questionType }}</el-tag>
              <el-tag size="small" :type="difficultyType(question.difficulty)">{{ difficultyLabel(question.difficulty) }}</el-tag>
              <span class="q-score">{{ question.score }}分</span>
            </div>
            <div class="q-content">{{ question.content }}</div>

            <div v-if="question.questionType === '单选题'" class="q-options">
              <el-radio-group v-model="answers[question.id]" @change="saveAnswer(question.id)">
                <el-radio v-for="opt in question.options" :key="opt.id" :value="opt.optionLabel">
                  {{ opt.optionLabel }}. {{ opt.optionContent }}
                </el-radio>
              </el-radio-group>
            </div>

            <div v-else-if="question.questionType === '多选题'" class="q-options">
              <el-checkbox-group v-model="multiAnswers[question.id]" @change="saveAnswer(question.id)">
                <el-checkbox v-for="opt in question.options" :key="opt.id" :value="opt.optionLabel">
                  {{ opt.optionLabel }}. {{ opt.optionContent }}
                </el-checkbox>
              </el-checkbox-group>
            </div>

            <div v-else-if="question.questionType === '判断题'" class="q-options">
              <el-radio-group v-model="answers[question.id]" @change="saveAnswer(question.id)">
                <el-radio value="true">正确</el-radio>
                <el-radio value="false">错误</el-radio>
              </el-radio-group>
            </div>

            <div v-else class="q-input">
              <el-input v-model="answers[question.id]" type="textarea" :rows="3" placeholder="请输入答案" @blur="saveAnswer(question.id)" />
            </div>
          </div>
        </div>
      </div>

      <div class="nav-area">
        <el-card>
          <template #header>答题进度</template>
          <div class="nav-grid">
            <div v-for="(q, index) in examInfo.questions" :key="q.id" class="nav-item"
                 :class="{ answered: isAnswered(q.id), current: currentIndex === index }"
                 @click="currentIndex = index">
              {{ index + 1 }}
            </div>
          </div>
          <div class="nav-legend">
            <span><span class="dot answered-dot"></span> 已答 {{ answeredCount }} 题</span>
            <span style="margin-top: 8px"><span class="dot unanswered-dot"></span> 未答 {{ examInfo.questions.length - answeredCount }} 题</span>
          </div>
        </el-card>

        <div class="submit-area">
          <el-button type="primary" size="large" @click="handleSubmit">交卷</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { examAPI } from '@/api'

const route = useRoute()
const router = useRouter()
const paperId = route.params.paperId

const examInfo = ref(null)
const answers = reactive({})
const multiAnswers = reactive({})
const currentIndex = ref(0)
const remainingTime = ref(0)
const tabSwitchCount = ref(0)
let timer = null
let recordId = null

const answeredCount = computed(() => {
  let count = 0
  for (const q of examInfo.value?.questions || []) {
    if (q.questionType === '多选题') {
      if (multiAnswers[q.id] && multiAnswers[q.id].length > 0) count++
    } else {
      if (answers[q.id]) count++
    }
  }
  return count
})

const difficultyLabel = (d) => ({ 1: '简单', 2: '中等', 3: '困难' }[d] || '')
const difficultyType = (d) => ({ 1: 'success', 2: 'warning', 3: 'danger' }[d] || '')

const isAnswered = (qId) => {
  if (multiAnswers[qId] && multiAnswers[qId].length > 0) return true
  if (answers[qId]) return true
  return false
}

const formatTime = (seconds) => {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const saveAnswer = async (questionId) => {
  if (!recordId) return
  try {
    let userAnswer = answers[questionId]
    if (multiAnswers[questionId]) {
      userAnswer = multiAnswers[questionId].join(',')
    }
    await examAPI.saveAnswer({ recordId, questionId, userAnswer })
  } catch (e) {
    console.error('保存答案失败', e)
  }
}

const handleVisibilityChange = async () => {
  if (document.hidden && recordId) {
    tabSwitchCount.value++
    ElMessage.warning(`检测到切屏，已记录（第 ${tabSwitchCount.value} 次）`)
    try {
      await examAPI.reportTabSwitch(recordId)
    } catch (e) {
      console.error('上报切屏失败', e)
    }
  }
}

const loadExam = async () => {
  try {
    const infoRes = await examAPI.getInfo(paperId)
    examInfo.value = infoRes.data
    remainingTime.value = examInfo.value.duration * 60

    const startRes = await examAPI.start(paperId)
    recordId = startRes.data.id

    const progressRes = await examAPI.getProgress(recordId)
    const progress = progressRes.data || {}
    for (const [qId, ans] of Object.entries(progress)) {
      const id = Number(qId)
      answers[id] = ans
    }

    startTimer()
  } catch (e) {
    ElMessage.error(e.message || '加载考试信息失败')
    router.push('/dashboard')
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      ElMessage.warning('考试时间到，系统将自动交卷')
      doSubmit()
    }
  }, 1000)
}

const handleSubmit = async () => {
  const unanswered = examInfo.value.questions.length - answeredCount.value
  let confirmText = '确定要交卷吗？'
  if (unanswered > 0) {
    confirmText = `还有 ${unanswered} 道题未答，确定要交卷吗？`
  }

  await ElMessageBox.confirm(confirmText, '交卷确认', { type: 'warning' })
  doSubmit()
}

const doSubmit = async () => {
  clearInterval(timer)
  try {
    await examAPI.submit(recordId)
    ElMessage.success('试卷已提交')
    router.push('/examHistory')
  } catch (e) {
    ElMessage.error('提交失败')
  }
}

onMounted(() => {
  loadExam()
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.exam-page { height: 100vh; background: #f0f2f5; display: flex; flex-direction: column; }
.exam-header {
  background: #fff; padding: 16px 24px; display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}
.exam-title h2 { margin: 0; font-size: 20px; }
.exam-title p { margin: 4px 0 0; color: #999; font-size: 13px; }
.exam-timer { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: bold; color: #333; }
.time-warning { color: #f56c6c; }
.exam-content { flex: 1; display: flex; gap: 20px; padding: 20px; overflow: hidden; }
.question-area {
  flex: 1;
  overflow-y: auto;
  &::-webkit-scrollbar{
    display:none;
  }
}
.question-card {
  background: #fff; border-radius: 8px; padding: 20px; margin-bottom: 16px;
  cursor: pointer; border: 2px solid transparent;
  transition: all 0.2s;
}
.question-card.active { border-color: #409eff; }
.q-num { font-size: 16px; font-weight: bold; margin-bottom: 8px; }
.q-meta { display: flex; gap: 8px; align-items: center; margin-bottom: 8px; }
.q-score { color: #409eff; margin-left: auto; }
.q-content { font-size: 15px; line-height: 1.8; margin-bottom: 16px; }
.q-options { display: flex; flex-direction: column; gap: 12px; }
.q-input { margin-top: 12px; }
.nav-area { width: 280px; flex-shrink: 0; display: flex; flex-direction: column; gap: 16px; }
.nav-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.nav-item {
  width: 36px; height: 36px; display: flex; align-items: center; justify-content: center;
  border-radius: 4px; background: #eee; font-size: 12px; cursor: pointer; border: 2px solid transparent;
}
.nav-item.answered { background: #e6f7e6; color: #67c23a; }
.nav-item.current { border-color: #409eff; background: #ecf5ff; }
.nav-legend { margin-top: 16px; font-size: 13px; display: flex; flex-direction: column; color: #666; }
.dot { display: inline-block; width: 12px; height: 12px; border-radius: 50%; margin-right: 4px; }
.answered-dot { background: #67c23a; }
.unanswered-dot { background: #eee; border: 1px solid #ccc; }
.submit-area { margin-top: auto; }
</style>
