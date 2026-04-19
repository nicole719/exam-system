package com.exam.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷响应数据传输对象
 * 用于返回试卷详情，包含关联的题目列表
 */
@Data
public class PaperVO {
    /** 试卷ID */
    private Long id;
    /** 试卷标题 */
    private String title;
    /** 所属学科 */
    private String subject;
    /** 适用年级 */
    private String gradeLevel;
    /** 试卷总分 */
    private Integer totalScore;
    /** 考试时长（分钟） */
    private Integer duration;
    /** 考试开始时间 */
    private LocalDateTime startTime;
    /** 考试结束时间 */
    private LocalDateTime endTime;
    /** 试卷状态 */
    private String status;
    /** 试卷说明 */
    private String description;
    /** 题目列表 */
    private List<QuestionVO> questions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionVO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionVO> questions) {
        this.questions = questions;
    }
}
