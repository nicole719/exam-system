package com.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷创建/更新请求数据传输对象
 * 用于接收试卷的完整信息，包括关联的题目ID列表
 */
@Data
public class PaperDTO {
    /** 试卷ID（更新时使用） */
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
    /** 试卷说明 */
    private String description;
    /** 关联的题目ID列表 */
    private List<Long> questionIds;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }
}
