package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 考试记录实体类
 * 记录用户参加考试的整体情况，包括成绩和考试状态
 */
@Data
@TableName("exam_record")
public class ExamRecord {
    /** 考试记录唯一标识，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 参考用户ID，关联sys_user表 */
    private Long userId;
    /** 试卷ID，关联exam_paper表 */
    private Long paperId;
    /** 考试总成绩 */
    private Double score;
    /** 客观题得分（选择题、判断题） */
    private Double objectiveScore;
    /** 主观题得分（简答题等） */
    private Double subjectiveScore;
    /** 考试状态：not_started-未开始，ongoing-进行中，submitted-已提交，graded-已批改 */
    private String status;
    /** 考试开始时间 */
    private LocalDateTime startTime;
    /** 提交时间 */
    private LocalDateTime submitTime;
    /** 参考者IP地址 */
    private String ipAddress;
    /** 记录创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;

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

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
