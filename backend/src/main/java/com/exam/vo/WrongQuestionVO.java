package com.exam.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 错题本响应数据传输对象
 * 包含错题记录信息及关联的题目详情
 */
@Data
public class WrongQuestionVO {
    /** 错题记录ID */
    private Long id;
    /** 题目ID */
    private Long questionId;
    /** 题目内容 */
    private String content;
    /** 题型 */
    private String questionType;
    /** 正确答案 */
    private String answer;
    /** 错误次数 */
    private Integer wrongCount;
    /** 最近练习时间 */
    private LocalDateTime lastPracticeTime;
}
