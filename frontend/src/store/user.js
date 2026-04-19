import { defineStore } from 'pinia'
import Cookies from 'js-cookie'
import { authAPI } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: Cookies.get('token') || '',
    userInfo: null
  }),
  getters: {
    isLoggedIn: state => !!state.token,
    role: state => state.userInfo?.role || '',
    isAdmin: state => state.userInfo?.role === 'ADMIN',
    isTeacher: state => state.userInfo?.role === 'TEACHER',
    isStudent: state => state.userInfo?.role === 'STUDENT',
    userId: state => state.userInfo?.id
  },
  actions: {
    async login(loginForm) {
      const res = await authAPI.login(loginForm)
      this.token = res.data.token
      this.userInfo = res.data
      Cookies.set('token', this.token, { expires: 7 })
      return res.data
    },
    async getCurrentUser() {
      if (!this.token) return null
      try {
        const res = await authAPI.getCurrentUser()
        this.userInfo = res.data
        return res.data
      } catch (e) {
        this.logout()
        return null
      }
    },
    logout() {
      this.token = ''
      this.userInfo = null
      Cookies.remove('token')
    },
    setToken(token) {
      this.token = token
      Cookies.set('token', token, { expires: 7 })
    }
  }
})
