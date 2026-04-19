package com.exam.service;

import java.util.List;
import java.util.Map;

/**
 * 统计分析业务逻辑接口
 * 定义系统数据的统计分析功能，提供各类统计报表和趋势分析
 */
public interface StatisticsService {
    /**
     * 获取系统概览统计
     * @return 包含用户数、题目数、试卷数、考试次数、平均分、及格率等
     */
    Map<String, Object> getOverview();

    /**
     * 获取学生个人统计
     * @param userId 用户ID
     * @return 包含参考次数、平均分、近10次考试记录
     */
    Map<String, Object> getStudentStatistics(Long userId);

    /**
     * 获取教师个人统计
     * @param userId 用户ID（创建人ID）
     * @return 包含发布试卷数、参考次数、平均成绩
     */
    Map<String, Object> getTeacherStatistics(Long userId);

    /**
     * 获取排行榜
     * @param subject 学科（可选）
     * @param gradeLevel 年级（可选）
     * @param limit 返回条数
     * @return 用户ID、姓名、总分列表
     */
    List<Map<String, Object>> getRanking(String subject, String gradeLevel, Integer limit);

    /**
     * 获取考试趋势数据
     * @param days 统计天数（默认30天）
     * @return 包含日期、考试次数、平均分
     */
    Map<String, Object> getExamTrend(Integer days);
}
