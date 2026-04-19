-- 小初高在线考试系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS exam_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE exam_db;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender INT COMMENT '性别 1男 0女',
    avatar VARCHAR(255) COMMENT '头像',
    role VARCHAR(20) NOT NULL COMMENT '角色 ADMIN/TEACHER/STUDENT',
    status INT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0未删 1已删'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题库表
DROP TABLE IF EXISTS question_bank;
CREATE TABLE question_bank (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    grade_level VARCHAR(20) NOT NULL COMMENT '年级',
    question_type VARCHAR(20) NOT NULL COMMENT '题型',
    difficulty INT DEFAULT 2 COMMENT '难度 1简单 2中等 3困难',
    content TEXT NOT NULL COMMENT '题目内容',
    answer TEXT COMMENT '正确答案',
    analysis TEXT COMMENT '答案解析',
    score INT DEFAULT 5 COMMENT '分值',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目选项表
DROP TABLE IF EXISTS question_option;
CREATE TABLE question_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '题目ID',
    option_label VARCHAR(10) NOT NULL COMMENT '选项标签 A/B/C/D',
    option_content VARCHAR(500) NOT NULL COMMENT '选项内容',
    is_correct TINYINT DEFAULT 0 COMMENT '是否正确',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 试卷表
DROP TABLE IF EXISTS exam_paper;
CREATE TABLE exam_paper (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '试卷标题',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    grade_level VARCHAR(20) NOT NULL COMMENT '年级',
    total_score INT DEFAULT 0 COMMENT '总分',
    duration INT DEFAULT 60 COMMENT '时长(分钟)',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态 DRAFT/PUBLISHED/CLOSED',
    description TEXT COMMENT '描述',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 试卷题目关联表
DROP TABLE IF EXISTS paper_question;
CREATE TABLE paper_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    sort_order INT DEFAULT 1 COMMENT '排序',
    score INT DEFAULT 5 COMMENT '分值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 考试记录表
DROP TABLE IF EXISTS exam_record;
CREATE TABLE exam_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    score DECIMAL(10,2) DEFAULT 0 COMMENT '总分',
    objective_score DECIMAL(10,2) DEFAULT 0 COMMENT '客观题得分',
    subjective_score DECIMAL(10,2) DEFAULT 0 COMMENT '主观题得分',
    status VARCHAR(20) DEFAULT 'ING' COMMENT '状态 ING/SUBMITTED',
    start_time DATETIME COMMENT '开始时间',
    submit_time DATETIME COMMENT '交卷时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 答题详情表
DROP TABLE IF EXISTS exam_answer;
CREATE TABLE exam_answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '考试记录ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    user_answer TEXT COMMENT '用户答案',
    is_correct TINYINT DEFAULT 0 COMMENT '是否正确',
    score DECIMAL(10,2) DEFAULT 0 COMMENT '得分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 错题本表
DROP TABLE IF EXISTS wrong_question;
CREATE TABLE wrong_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    wrong_count INT DEFAULT 1 COMMENT '错误次数',
    last_practice_time DATETIME COMMENT '最近练习时间',
    wrong_reason TEXT COMMENT '错误原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 班级表
DROP TABLE IF EXISTS class_info;
CREATE TABLE class_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    grade_level VARCHAR(20) NOT NULL COMMENT '年级',
    teacher_id BIGINT COMMENT '班主任ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认用户 (密码均为 123456，BCrypt加密)
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 1),
('teacher', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', 'TEACHER', 1),
('student', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李同学', 'STUDENT', 1);

-- 插入示例题目
INSERT INTO question_bank (subject, grade_level, question_type, difficulty, content, answer, analysis, score) VALUES
('数学', '初中', '单选题', 1, '下列哪个数是偶数？', 'B', '能被2整除的数是偶数', 5),
('数学', '初中', '单选题', 1, '3 + 5 = ?', 'C', '简单的加法运算', 5),
('数学', '初中', '判断题', 1, '所有的正方形都是矩形。', 'true', '正方形符合矩形的所有定义', 5),
('数学', '初中', '填空题', 2, '一元二次方程 x^2 - 4 = 0 的解为 x = ____。', '±2', '移项得 x^2 = 4，开方得 x = ±2', 10),
('语文', '初中', '单选题', 2, '《静夜思》的作者是谁？', 'A', '《静夜思》是唐代诗人李白的作品', 5);

INSERT INTO question_option (question_id, option_label, option_content, is_correct) VALUES
(1, 'A', '3', 0),
(1, 'B', '5', 1),
(1, 'C', '7', 0),
(1, 'D', '9', 0),
(2, 'A', '6', 0),
(2, 'B', '7', 0),
(2, 'C', '8', 1),
(2, 'D', '9', 0),
(5, 'A', '李白', 1),
(5, 'B', '杜甫', 0),
(5, 'C', '白居易', 0),
(5, 'D', '王维', 0);

-- 插入示例试卷
INSERT INTO exam_paper (title, subject, grade_level, total_score, duration, status, description) VALUES
('初中数学单元测试', '数学', '初中', 20, 60, 'PUBLISHED', '初中数学第一单元综合测试');

INSERT INTO paper_question (paper_id, question_id, sort_order, score) VALUES
(1, 1, 1, 5),
(1, 2, 2, 5),
(1, 3, 3, 5),
(1, 4, 4, 10);
