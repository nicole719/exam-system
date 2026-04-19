# 小初高在线考试系统

## 项目简介

基于 SpringBoot + Vue 3 的小初高在线考试系统，支持学生在线考试、教师组卷出题、管理员系统管理等功能。

## 技术栈

### 后端
- SpringBoot 3.2.4
- MyBatis-Plus 3.5.6
- Spring Security 6.x
- JWT (jjwt 0.12.5)
- Redis
- MySQL 8.x

### 前端
- Vue 3.x
- Element Plus
- Pinia (状态管理)
- Vue Router 4.x
- Axios
- ECharts

## 项目结构

```
exam-system/
├── backend/           # SpringBoot 后端
│   ├── src/main/java/com/exam/
│   │   ├── config/   # 配置类
│   │   ├── controller/  # 控制器
│   │   ├── service/     # 业务层
│   │   ├── mapper/      # 数据访问层
│   │   ├── entity/      # 实体类
│   │   ├── dto/         # 数据传输对象
│   │   ├── vo/          # 视图对象
│   │   ├── security/    # 安全认证
│   │   └── utils/       # 工具类
│   └── pom.xml
├── frontend/          # Vue 3 前端
│   ├── src/
│   │   ├── api/       # 接口调用
│   │   ├── router/    # 路由配置
│   │   ├── store/      # 状态管理
│   │   ├── views/      # 页面组件
│   │   └── ...
│   └── package.json
└── SPEC.md            # 设计文档
```

## 快速启动

### 1. 导入数据库

创建 MySQL 数据库 `exam_db`，导入 `sql/init.sql`（如有）。

### 2. 修改配置

编辑 `backend/src/main/resources/application.yml` 中的数据库和 Redis 连接信息。

### 3. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 5. 访问系统

- 前端地址: http://localhost:3000
- 后端接口: http://localhost:8080/api

## 功能模块

| 模块 | 描述 |
|------|------|
| 用户管理 | 用户注册、登录、角色分配、状态管理 |
| 题库管理 | 单选/多选/判断/填空/简答题 CRUD |
| 试卷管理 | 手动组卷、自动组卷、发布管理 |
| 在线考试 | 答题计时、实时保存、答题导航 |
| 成绩管理 | 自动评分、成绩查询、导出 |
| 错题本 | 错题收录、重练、错因记录 |
| 数据统计 | 可视化图表、成绩排行、趋势分析 |

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher | 123456 |
| 学生 | student | 123456 |
