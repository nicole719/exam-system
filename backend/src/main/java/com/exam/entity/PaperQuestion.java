package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 试卷题目关联实体类
 * 建立试卷与题库的关联关系，记录每份试卷包含哪些题目及分值
 */
@Data
@TableName("paper_question")
public class PaperQuestion {
    /** 关联记录唯一标识，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 试卷ID，关联exam_paper表 */
    private Long paperId;
    /** 题目ID，关联question_bank表 */
    private Long questionId;
    /** 题目在试卷中的排序顺序 */
    private Integer sortOrder;
    /** 该题在试卷中的分值 */
    private Integer score;
    /** 记录创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
