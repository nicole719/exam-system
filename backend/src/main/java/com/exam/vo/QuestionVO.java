package com.exam.vo;

import lombok.Data;
import java.util.List;

/**
 * 题目响应数据传输对象
 * 用于返回题目详情，包含选项列表
 */
@Data
public class QuestionVO {
    /** 题目ID */
    private Long id;
    /** 所属学科 */
    private String subject;
    /** 适用年级 */
    private String gradeLevel;
    /** 题目类型 */
    private String questionType;
    /** 难度等级名称 */
    private String difficultyName;
    /** 题目内容/题干 */
    private String content;
    /** 选项列表 */
    private List<OptionVO> options;
    /** 标准答案（可能为空，用于隐藏答案） */
    private String answer;
    /** 答案解析 */
    private String analysis;
    /** 题目分值 */
    private Integer score;

    /**
     * 选项响应数据传输对象
     */
    @Data
    public static class OptionVO {
        /** 选项ID */
        private Long id;
        /** 选项标签（A/B/C/D） */
        private String optionLabel;
        /** 选项内容 */
        private String optionContent;
        /** 是否为正确答案（可能为空，用于隐藏答案） */
        private Boolean isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDifficultyName() {
        return difficultyName;
    }

    public void setDifficultyName(String difficultyName) {
        this.difficultyName = difficultyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<OptionVO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionVO> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
