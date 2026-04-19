# 在线考试系统 SPEC.md

## 1. 项目概述

### 项目名称
小初高在线考试系统 (ExamOnline)

### 核心技术栈
- **后端**: SpringBoot 3.x + MyBatis-Plus 3.x + Spring Security 6.x + JWT + Redis
- **前端**: Vue 3.x + Element Plus + Axios + Pinia + Vue Router 4.x
- **数据库**: MySQL 8.x
- **缓存**: Redis

### 系统目标
覆盖小初高在线考试全流程，为学生、教师、管理员提供完整的在线考试解决方案。

---

## 2. 用户角色与权限

| 角色 | 权限描述 |
|------|---------|
| **管理员 (ADMIN)** | 用户管理（CRUD）、题库管理、试卷管理、数据统计、系统配置 |
| **教师 (TEACHER)** | 题库管理（所属科目）、试卷管理（组卷/发布）、成绩管理、查看统计 |
| **学生 (STUDENT)** | 在线考试、成绩查询、错题本练习 |

---

## 3. 功能模块设计

### 3.1 用户管理模块
- 用户注册（学生）/ 管理员创建（教师/管理员）
- 用户登录（JWT 无状态认证）
- 个人信息管理
- 密码修改
- 用户启用/禁用（管理员）
- 角色分配

### 3.2 题库管理模块
- 单选题、多选题、判断题、填空题、简答题
- 按科目、年级、难度筛选
- 题目CRUD（教师/管理员）
- 题目批量导入/导出
- 题目难度标签（简单/中等/困难）

### 3.3 试卷管理模块
- 手动组卷（教师选择题目）
- 自动组卷（按知识点/难度/题量自动抽取）
- 试卷预览与编辑
- 试卷发布（指定考试时间/班级）
- 试卷模板管理

### 3.4 在线考试模块
- 考试须知页面
- 答题界面（实时计时、自动保存答案）
- 题目导航（已答/未答标记）
- 交卷确认
- 断线重连（Redis 缓存答题进度）

### 3.5 成绩管理模块
- 客观题自动评分
- 主观题教师批改
- 成绩排名（班级/年级）
- 成绩导出（Excel）
- 成绩分析报告

### 3.6 错题本模块
- 自动收录错题
- 错题重练
- 错因分析记录
- 薄弱知识点标记

### 3.7 数据统计模块
- 学生学习情况仪表盘
- 教师任教班级成绩统计
- 管理员全局数据概览（考试次数、参考率、平均分、及格率）
- 可视化图表（ECharts）

---

## 4. 数据库设计

### 4.1 ER 关系概览
```
用户 (sys_user) ← 考试记录 (exam_record) → 试卷 (exam_paper)
                    ↓
              答题详情 (exam_answer)
                    ↓
              题目 (question_bank) → 题目选项 (question_option)

错题本 (wrong_question) ← 题目 (question_bank)
试卷 (exam_paper) → 试卷题目关联 (paper_question)
班级 (class_info) ← 用户班级关联 (user_class)
```

### 4.2 核心数据表

**sys_user** - 用户表
- id, username, password, real_name, phone, email, gender, avatar, role, status, create_time, update_time

**question_bank** - 题库表
- id, subject, grade_level, question_type, difficulty, content, answer, analysis, score, create_user_id, create_time, update_time

**question_option** - 题目选项表（用于单选/多选）
- id, question_id, option_label, option_content, is_correct

**exam_paper** - 试卷表
- id, title, subject, grade_level, total_score, duration, start_time, end_time, status, create_user_id, create_time, update_time

**paper_question** - 试卷题目关联表
- id, paper_id, question_id, sort_order, score

**exam_record** - 考试记录表
- id, user_id, paper_id, score, objective_score, subjective_score, status, start_time, submit_time, ip_address

**exam_answer** - 答题详情表
- id, record_id, question_id, user_answer, is_correct, score, create_time

**wrong_question** - 错题本表
- id, user_id, question_id, wrong_count, last_practice_time, create_time

**class_info** - 班级表
- id, class_name, grade_level, teacher_id, create_time

**data_statistics** - 数据统计表
- id, stat_date, exam_count, user_count, avg_score, pass_rate

---

## 5. 后端 API 设计

### 5.1 认证模块
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 登出
- `GET /api/auth/currentUser` - 获取当前用户信息

### 5.2 用户管理
- `GET /api/user/list` - 用户列表（分页）
- `POST /api/user` - 创建用户
- `PUT /api/user/{id}` - 更新用户
- `DELETE /api/user/{id}` - 删除用户
- `PUT /api/user/password` - 修改密码

### 5.3 题库管理
- `GET /api/question/list` - 题目列表（分页筛选）
- `POST /api/question` - 创建题目
- `PUT /api/question/{id}` - 更新题目
- `DELETE /api/question/{id}` - 删除题目
- `POST /api/question/import` - 批量导入
- `GET /api/question/export` - 导出题目

### 5.4 试卷管理
- `GET /api/paper/list` - 试卷列表
- `POST /api/paper` - 创建试卷
- `PUT /api/paper/{id}` - 更新试卷
- `DELETE /api/paper/{id}` - 删除试卷
- `POST /api/paper/publish/{id}` - 发布试卷
- `POST /api/paper/autoGenerate` - 自动组卷

### 5.5 在线考试
- `GET /api/exam/info/{paperId}` - 获取考试信息
- `POST /api/exam/start/{paperId}` - 开始考试
- `POST /api/exam/saveAnswer` - 保存答题进度（Redis 缓存）
- `POST /api/exam/submit/{recordId}` - 提交试卷
- `GET /api/exam/history` - 考试历史记录

### 5.6 成绩管理
- `GET /api/score/list` - 成绩列表
- `GET /api/score/detail/{recordId}` - 成绩详情
- `POST /api/score/review/{recordId}` - 教师批改
- `GET /api/score/export` - 导出成绩

### 5.7 错题本
- `GET /api/wrong/list` - 错题列表
- `POST /api/wrong/practice/{id}` - 错题练习
- `POST /api/wrong/record` - 记录错因

### 5.8 数据统计
- `GET /api/statistics/overview` - 全局概览
- `GET /api/statistics/student/{userId}` - 学生统计
- `GET /api/statistics/teacher/{userId}` - 教师统计
- `GET /api/statistics/ranking` - 成绩排名

---

## 6. 前端页面设计

### 6.1 路由结构
```
/login              - 登录页
/register           - 注册页
/dashboard          - 工作台/仪表盘
/user               - 用户管理（管理员）
/question           - 题库管理（教师/管理员）
/paper              - 试卷管理（教师/管理员）
/exam/:paperId      - 在线考试（学生）
/examHistory        - 考试历史（学生）
/score              - 成绩管理
/wrong              - 错题本（学生）
/statistics         - 数据统计（教师/管理员）
```

### 6.2 组件清单
- Layout: MainLayout, HeaderNav, SideMenu
- Common: Pagination, TableList, SearchForm, UploadFile, ConfirmDialog
- Question: QuestionEditor, QuestionPreview, QuestionOptionItem
- Exam: ExamHeader, QuestionCard, AnswerNavigation, TimerBar
- Statistics: StatCard, ChartLine, ChartBar, ChartPie

---

## 7. 安全设计

### 7.1 JWT 认证
- Token 有效期: 7 天（可配置）
- Refresh Token: 14 天
- Token 存储: localStorage

### 7.2 权限控制
- 接口级权限: `@PreAuthorize("hasRole('ADMIN')")`
- 路由守卫: 登录状态 + 角色检查

### 7.3 Redis 缓存
- 用户登录 Token 缓存
- 答题进度实时缓存（防丢失）
- 热点数据缓存（题目、试卷）

---

## 8. 项目结构

### 后端 (backend)
```
backend/
├── pom.xml
├── src/main/java/com/exam/
│   ├── ExamApplication.java
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── service/         # 业务层
│   ├── mapper/          # 数据访问层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── vo/              # 视图对象
│   ├── security/        # 安全认证
│   └── utils/           # 工具类
└── src/main/resources/
    ├── application.yml
    └── mapper/
```

### 前端 (frontend)
```
frontend/
├── package.json
├── vite.config.js
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── api/             # 接口调用
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── utils/           # 工具函数
│   └── views/           # 页面视图
```

---

## 9. 验收标准

1. 后端所有 RESTful API 可正常访问并返回正确数据
2. 前端页面完整覆盖七大模块，交互流畅
3. 登录认证流程完整，JWT Token 正确验证
4. 考试流程：开始→答题→交卷→评分 完整闭环
5. 错题本功能正常，可进行错题重练
6. 数据统计页面展示可视化图表
7. Redis 缓存生效，答题进度可持久化
8. 代码编译无错误，前后端均可正常启动
