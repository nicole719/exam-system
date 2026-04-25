import request from './request'

export const authAPI = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  logout: () => request.post('/auth/logout'),
  getCurrentUser: () => request.get('/auth/currentUser')
}

export const userAPI = {
  list: (params) => request.get('/user/list', { params }),
  create: (data) => request.post('/user', data),
  update: (id, data) => request.put(`/user/${id}`, data),
  delete: (id) => request.delete(`/user/${id}`),
  changeStatus: (id, status) => request.put(`/user/status/${id}?status=${status}`)
}

export const questionAPI = {
  list: (params) => request.get('/question/list', { params }),
  create: (data) => request.post('/question', data),
  update: (id, data) => request.put(`/question/${id}`, data),
  delete: (id) => request.delete(`/question/${id}`),
  detail: (id) => request.get(`/question/${id}`),
  autoGenerate: (params) => request.post('/question/autoGenerate', null, { params })
}

export const paperAPI = {
  list: (params) => request.get('/paper/list', { params }),
  create: (data) => request.post('/paper', data),
  update: (id, data) => request.put(`/paper/${id}`, data),
  delete: (id) => request.delete(`/paper/${id}`),
  publish: (id) => request.post(`/paper/publish/${id}`),
  close: (id) => request.post(`/paper/close/${id}`),
  detail: (id) => request.get(`/paper/${id}`),
  available: () => request.get('/paper/available')
}

export const examAPI = {
  getInfo: (paperId) => request.get(`/exam/info/${paperId}`),
  start: (paperId) => request.post(`/exam/start/${paperId}`),
  saveAnswer: (data) => request.post('/exam/saveAnswer', data),
  getProgress: (recordId) => request.get(`/exam/progress/${recordId}`),
  submit: (recordId) => request.post(`/exam/submit/${recordId}`),
  history: (params) => request.get('/exam/history', { params }),
  detail: (recordId) => request.get(`/exam/detail/${recordId}`),
  reportTabSwitch: (data) => request.post('/exam/reportTabSwitch', data)
}

export const scoreAPI = {
  list: (params) => request.get('/score/list', { params }),
  detail: (recordId) => request.get(`/score/detail/${recordId}`)
}

export const wrongAPI = {
  list: (params) => request.get('/wrong/list', { params }),
  questions: () => request.get('/wrong/questions'),
  practice: (id) => request.post(`/wrong/practice/${id}`),
  record: (data) => request.post('/wrong/record', data),
  remove: (id) => request.delete(`/wrong/${id}`)
}

export const statisticsAPI = {
  overview: () => request.get('/statistics/overview'),
  studentStats: (userId) => request.get(`/statistics/student/${userId}`),
  teacherStats: (userId) => request.get(`/statistics/teacher/${userId}`),
  ranking: (params) => request.get('/statistics/ranking', { params }),
  trend: (days) => request.get('/statistics/trend', { params: { days } })
}
