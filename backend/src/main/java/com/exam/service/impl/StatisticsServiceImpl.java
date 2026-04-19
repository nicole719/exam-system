package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计分析业务逻辑实现类
 * 实现系统数据的统计分析功能，包括概览统计、排行榜、趋势分析等
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private ExamPaperMapper examPaperMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    /**
     * 获取系统概览统计
     * 统计用户数、题目数、试卷数、考试次数，计算平均分和及格率
     */
    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        long userCount = sysUserMapper.selectCount(null);
        long questionCount = questionBankMapper.selectCount(null);
        long paperCount = examPaperMapper.selectCount(null);
        long examCount = examRecordMapper.selectCount(
            new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStatus, "SUBMITTED")
        );

        result.put("userCount", userCount);
        result.put("questionCount", questionCount);
        result.put("paperCount", paperCount);
        result.put("examCount", examCount);

        List<ExamRecord> records = examRecordMapper.selectList(
            new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStatus, "SUBMITTED")
        );
        double avgScore = 0;
        double passRate = 0;
        if (!records.isEmpty()) {
            double totalScore = records.stream().mapToDouble(r -> r.getScore() != null ? r.getScore() : 0).sum();
            avgScore = totalScore / records.size();
            long passCount = records.stream().filter(r -> r.getScore() != null && r.getScore() >= 60).count();
            passRate = (double) passCount / records.size() * 100;
        }
        result.put("avgScore", Math.round(avgScore * 100) / 100.0);
        result.put("passRate", Math.round(passRate * 100) / 100.0);

        return result;
    }

    /**
     * 获取学生个人统计
     * 统计参考次数、平均分和最近10次考试记录
     */
    @Override
    public Map<String, Object> getStudentStatistics(Long userId) {
        Map<String, Object> result = new HashMap<>();

        List<ExamRecord> records = examRecordMapper.selectList(
            new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getUserId, userId)
                .eq(ExamRecord::getStatus, "SUBMITTED")
        );
        result.put("totalExams", records.size());

        double avgScore = 0;
        if (!records.isEmpty()) {
            avgScore = records.stream().mapToDouble(r -> r.getScore() != null ? r.getScore() : 0).average().orElse(0);
        }
        result.put("avgScore", Math.round(avgScore * 100) / 100.0);

        List<Map<String, Object>> recentExams = records.stream().limit(10).map(r -> {
            Map<String, Object> m = new HashMap<>();
            ExamPaper paper = examPaperMapper.selectById(r.getPaperId());
            m.put("paperTitle", paper != null ? paper.getTitle() : "");
            m.put("score", r.getScore());
            m.put("submitTime", r.getSubmitTime());
            return m;
        }).collect(Collectors.toList());
        result.put("recentExams", recentExams);

        return result;
    }

    /**
     * 获取教师个人统计
     * 统计发布的试卷数、参考次数、平均成绩
     */
    @Override
    public Map<String, Object> getTeacherStatistics(Long userId) {
        Map<String, Object> result = new HashMap<>();

        List<ExamPaper> papers = examPaperMapper.selectList(
            new LambdaQueryWrapper<ExamPaper>().eq(ExamPaper::getCreateUserId, userId)
        );
        result.put("totalPapers", papers.size());

        List<Long> paperIds = papers.stream().map(ExamPaper::getId).collect(Collectors.toList());
        long totalExams = 0;
        double avgScore = 0;

        if (!paperIds.isEmpty()) {
            List<ExamRecord> records = examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>()
                    .in(ExamRecord::getPaperId, paperIds)
                    .eq(ExamRecord::getStatus, "SUBMITTED")
            );
            totalExams = records.size();
            if (!records.isEmpty()) {
                avgScore = records.stream().mapToDouble(r -> r.getScore() != null ? r.getScore() : 0).average().orElse(0);
            }
        }
        result.put("totalExams", totalExams);
        result.put("avgScore", Math.round(avgScore * 100) / 100.0);

        return result;
    }

    /**
     * 获取排行榜
     * 按总分降序排列，可按学科和年级筛选
     */
    @Override
    public List<Map<String, Object>> getRanking(String subject, String gradeLevel, Integer limit) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getStatus, "SUBMITTED");
        if (subject != null && !subject.isEmpty()) {
            wrapper.in(ExamRecord::getPaperId,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaper>()
                    .eq(ExamPaper::getSubject, subject)
            );
        }
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        Map<Long, Double> userScoreMap = new HashMap<>();
        for (ExamRecord r : records) {
            Double existing = userScoreMap.get(r.getUserId());
            double score = r.getScore() != null ? r.getScore() : 0;
            if (existing == null) {
                userScoreMap.put(r.getUserId(), score);
            } else {
                userScoreMap.put(r.getUserId(), existing + score);
            }
        }

        return userScoreMap.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit != null ? limit : 20)
            .map(entry -> {
                Map<String, Object> m = new HashMap<>();
                SysUser user = sysUserMapper.selectById(entry.getKey());
                m.put("userId", entry.getKey());
                m.put("realName", user != null ? user.getRealName() : "");
                m.put("totalScore", entry.getValue());
                return m;
            }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getExamTrend(Integer days) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime startDate = LocalDateTime.now().minusDays(days != null ? days : 30);

        List<ExamRecord> records = examRecordMapper.selectList(
            new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getStatus, "SUBMITTED")
                .ge(ExamRecord::getSubmitTime, startDate)
        );

        Map<String, List<ExamRecord>> byDate = records.stream()
            .filter(r -> r.getSubmitTime() != null)
            .collect(Collectors.groupingBy(r -> r.getSubmitTime().toLocalDate().toString()));

        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        List<Double> avgScores = new ArrayList<>();

        byDate.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> {
                dates.add(e.getKey());
                counts.add(e.getValue().size());
                double avg = e.getValue().stream().mapToDouble(r -> r.getScore() != null ? r.getScore() : 0).average().orElse(0);
                avgScores.add(Math.round(avg * 100) / 100.0);
            });

        result.put("dates", dates);
        result.put("counts", counts);
        result.put("avgScores", avgScores);
        return result;
    }
}
