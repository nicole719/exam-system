package com.exam.dto;

import java.util.Map;

/**
 * 答题进度数据传输对象
 * 用于获取用户的答题进度
 */
public class AnswerProgressDTO {
    /** 考试记录ID */
    private Long recordId;
    /** 答题进度（题目ID到答案的映射） */
    private Map<Long, String> answers;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
