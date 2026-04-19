package com.exam.dto;

import lombok.Data;
import java.util.List;

/**
 * 题目创建/更新请求数据传输对象
 * 用于接收题目的完整信息，包括选项列表
 */
@Data
public class QuestionDTO {
    /** 题目ID（更新时使用） */
    private Long id;
    /** 所属学科 */
    private String subject;
    /** 适用年级 */
    private String gradeLevel;
    /** 题目类型 */
    private String questionType;
    /** 难度等级 */
    private Integer difficulty;
    /** 题目内容/题干 */
    private String content;
    /** 标准答案 */
    private String answer;
    /** 答案解析 */
    private String analysis;
    /** 题目分值 */
    private Integer score;
    /** 选项列表（用于选择题和判断题） */
    private List<OptionDTO> options;

    /**
     * 选项数据传输对象
     */
    @Data
    public static class OptionDTO {
        /** 选项标签（A/B/C/D） */
        private String optionLabel;
        /** 选项内容 */
        private String optionContent;
        /** 是否为正确答案 */
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

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }
}
