package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.dto.QuestionDTO;
import com.exam.entity.QuestionBank;
import com.exam.service.QuestionBankService;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.QuestionVO;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库管理控制器
 * 提供题目的增删改查、自动选题等功能，教师和管理员可操作
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionBankService questionBankService;

    /**
     * 分页查询题目列表
     * 支持按学科、年级、题型、难度进行筛选
     */
    @GetMapping("/list")
    public Result<IPage<QuestionBank>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) Integer difficulty) {
        return Result.success(questionBankService.pageList(pageNum, pageSize, subject, gradeLevel, questionType, difficulty));
    }

    /**
     * 创建题目
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<QuestionBank> create(@RequestBody QuestionDTO dto) {
        Long userId = getCurrentUserId();
        return Result.success(questionBankService.createQuestion(dto, userId));
    }

    /**
     * 更新题目信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<QuestionBank> update(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        dto.setId(id);
        return Result.success(questionBankService.updateQuestion(dto));
    }

    /**
     * 删除题目（级联删除选项，逻辑删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> delete(@PathVariable Long id) {
        questionBankService.deleteQuestion(id);
        return Result.success("删除成功",null);
    }

    /**
     * 获取题目详情（含选项信息）
     */
    @GetMapping("/{id}")
    public Result<QuestionVO> detail(@PathVariable Long id) {
        return Result.success(questionBankService.getQuestionDetail(id));
    }

    @PostMapping("/autoGenerate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<QuestionBank>> autoGenerate(
            @RequestParam String subject,
            @RequestParam String gradeLevel,
            @RequestParam String questionType,
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer difficulty) {
        return Result.success(questionBankService.autoGenerateQuestions(subject, gradeLevel, questionType, count, difficulty));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }
}
