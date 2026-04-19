package com.exam.controller;

import com.exam.service.StatisticsService;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器
 * 提供系统概览统计、个人统计、排行榜、趋势分析等功能
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取系统概览统计
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(statisticsService.getOverview());
    }

    /**
     * 获取学生个人统计
     */
    @GetMapping("/student/{userId}")
    public Result<Map<String, Object>> studentStats(@PathVariable Long userId) {
        return Result.success(statisticsService.getStudentStatistics(userId));
    }

    /**
     * 获取教师个人统计
     */
    @GetMapping("/teacher/{userId}")
    public Result<Map<String, Object>> teacherStats(@PathVariable Long userId) {
        return Result.success(statisticsService.getTeacherStatistics(userId));
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> ranking(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(statisticsService.getRanking(subject, gradeLevel, limit));
    }

    /**
     * 获取考试趋势数据
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(@RequestParam(defaultValue = "30") Integer days) {
        return Result.success(statisticsService.getExamTrend(days));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }
}
