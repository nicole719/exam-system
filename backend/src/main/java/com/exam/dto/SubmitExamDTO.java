package com.exam.dto;

/**
 * 提交考试请求数据传输对象
 * 用于提交试卷
 */
public class SubmitExamDTO {
    /** 考试记录ID */
    private Long recordId;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}
