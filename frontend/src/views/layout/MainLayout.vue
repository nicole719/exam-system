<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <img v-if="!isCollapse" src="@/assets/logo.png" alt="logo" @error="handleImgError" />
        <span v-if="!isCollapse">考试系统</span>
        <span v-else>考</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item v-if="isAdmin || isTeacher" index="/question">
          <el-icon><Document /></el-icon>
          <template #title>题库管理</template>
        </el-menu-item>
        <el-menu-item v-if="isAdmin || isTeacher" index="/paper">
          <el-icon><Tickets /></el-icon>
          <template #title>试卷管理</template>
        </el-menu-item>
        <el-menu-item v-if="isStudent" index="/examHistory">
          <el-icon><Reading /></el-icon>
          <template #title>考试记录</template>
        </el-menu-item>
        <el-menu-item index="/score">
          <el-icon><DataLine /></el-icon>
          <template #title>成绩管理</template>
        </el-menu-item>
        <el-menu-item v-if="isStudent" index="/wrong">
          <el-icon><Warning /></el-icon>
          <template #title>错题本</template>
        </el-menu-item>
        <el-menu-item v-if="isAdmin || isTeacher" index="/statistics">
          <el-icon><PieChart /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo?.avatar">
                {{ userInfo?.realName?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userInfo?.realName }}</span>
              <el-tag size="small" type="info">{{ roleLabel }}</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)
const isTeacher = computed(() => userStore.isTeacher)
const isStudent = computed(() => userStore.isStudent)

const roleLabel = computed(() => {
  const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return map[userStore.role] || ''
})

const currentTitle = computed(() => route.meta.title || '')
const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

const handleImgError = (e) => {
  e.target.style.display = 'none'
}

onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.userInfo) {
    await userStore.getCurrentUser()
  }
})
</script>

<style scoped>
.main-layout {
  height: 100vh;
}
.aside {
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #3d4a5c;
  overflow: hidden;
}
.logo img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}
.sidebar-menu {
  border-right: none;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.header-left {
  display: flex;
  align-items: center;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #666;
}
.collapse-btn:hover {
  color: #409eff;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 10px;
}
.username {
  font-size: 14px;
  color: #333;
}
.main-content {
  padding: 20px;
  background: #f0f2f5;
  overflow-y: auto;
}
</style>
