package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 错题记录实体类
 * 记录用户的错题信息，便于后续复习和巩固
 */
@Data
@TableName("wrong_question")
public class WrongQuestion {
    /** 错题记录唯一标识，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户ID，关联sys_user表 */
    private Long userId;
    /** 题目ID，关联question_bank表 */
    private Long questionId;
    /** 累计错误次数 */
    private Integer wrongCount;
    /** 最后一次练习时间 */
    private LocalDateTime lastPracticeTime;
    /** 错误原因说明 */
    private String wrongReason;
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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public LocalDateTime getLastPracticeTime() {
        return lastPracticeTime;
    }

    public void setLastPracticeTime(LocalDateTime lastPracticeTime) {
        this.lastPracticeTime = lastPracticeTime;
    }

    public String getWrongReason() {
        return wrongReason;
    }

    public void setWrongReason(String wrongReason) {
        this.wrongReason = wrongReason;
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
