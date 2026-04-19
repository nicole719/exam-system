package com.exam.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 考试记录响应数据传输对象
 * 用于返回考试记录的详情，包含关联的试卷信息和答题统计
 */
@Data
public class ExamRecordVO {
    /** 考试记录ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 用户姓名 */
    private String userName;
    /** 试卷ID */
    private Long paperId;
    /** 试卷标题 */
    private String paperTitle;
    /** 总成绩 */
    private Double score;
    /** 客观题得分 */
    private Double objectiveScore;
    /** 主观题得分 */
    private Double subjectiveScore;
    /** 考试状态 */
    private String status;
    /** 开始时间 */
    private LocalDateTime startTime;
    /** 提交时间 */
    private LocalDateTime submitTime;
    /** 总题数 */
    private Integer totalQuestions;
    /** 正确题数 */
    private Integer correctCount;
    /** 错误题数 */
    private Integer wrongCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getObjectiveScore() {
        return objectiveScore;
    }

    public void setObjectiveScore(Double objectiveScore) {
        this.objectiveScore = objectiveScore;
    }

    public Double getSubjectiveScore() {
        return subjectiveScore;
    }

    public void setSubjectiveScore(Double subjectiveScore) {
        this.subjectiveScore = subjectiveScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }
}
