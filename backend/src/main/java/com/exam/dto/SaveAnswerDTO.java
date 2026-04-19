package com.exam.dto;

/**
 * 保存答题请求数据传输对象
 * 用于保存用户提交的答案
 */
public class SaveAnswerDTO {
    /** 考试记录ID */
    private Long recordId;
    /** 题目ID */
    private Long questionId;
    /** 用户答案 */
    private String userAnswer;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
