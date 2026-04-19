package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题目选项实体类
 * 存储选择题目的选项信息，用于选择题型和判断题
 */
@Data
@TableName("question_option")
public class QuestionOption {
    /** 选项唯一标识，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 所属题目ID，关联question_bank表 */
    private Long questionId;
    /** 选项标签，如：A、B、C、D */
    private String optionLabel;
    /** 选项内容 */
    private String optionContent;
    /** 是否为正确答案：true-正确，false-错误 */
    private Boolean isCorrect;
    /** 记录创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public String getOptionContent() {
        return optionContent;
    }

    public void setOptionContent(String optionContent) {
        this.optionContent = optionContent;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
