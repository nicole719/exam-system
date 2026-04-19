import { createRouter, createWebHistory } from 'vue-router'
import Cookies from 'js-cookie'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/layout/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/layout/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/layout/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/UserManage.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'question',
        name: 'Question',
        component: () => import('@/views/question/QuestionManage.vue'),
        meta: { title: '题库管理', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'paper',
        name: 'Paper',
        component: () => import('@/views/paper/PaperManage.vue'),
        meta: { title: '试卷管理', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'paper/create',
        name: 'PaperCreate',
        component: () => import('@/views/paper/PaperCreate.vue'),
        meta: { title: '创建试卷', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'exam/:paperId',
        name: 'Exam',
        component: () => import('@/views/exam/ExamPage.vue'),
        meta: { title: '在线考试', roles: ['STUDENT'] }
      },
      {
        path: 'examHistory',
        name: 'ExamHistory',
        component: () => import('@/views/exam/ExamHistory.vue'),
        meta: { title: '考试记录', roles: ['STUDENT'] }
      },
      {
        path: 'score',
        name: 'Score',
        component: () => import('@/views/score/ScoreManage.vue'),
        meta: { title: '成绩管理' }
      },
      {
        path: 'wrong',
        name: 'Wrong',
        component: () => import('@/views/wrong/WrongBook.vue'),
        meta: { title: '错题本', roles: ['STUDENT'] }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/StatisticsView.vue'),
        meta: { title: '数据统计', roles: ['ADMIN', 'TEACHER'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 在线考试系统` : '在线考试系统'

  if (to.meta.guest) {
    if (Cookies.get('token')) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  if (to.meta.requiresAuth || to.meta.roles) {
    if (!Cookies.get('token')) {
      next('/login')
      return
    }
  }

  if (to.meta.roles) {
    const userStore = (await import('@/store/user')).useUserStore()
    if (!userStore.isLoggedIn) {
      await userStore.getCurrentUser()
    }
    if (!to.meta.roles.includes(userStore.role)) {
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
