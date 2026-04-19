package com.exam.dto;

/**
 * 开始考试请求数据传输对象
 * 用于接收开始考试时的参数信息
 */
public class ExamStartDTO {
    /** 试卷ID */
    private Long paperId;
    /** 参考者IP地址 */
    private String ipAddress;

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
