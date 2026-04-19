package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.dto.PaperDTO;
import com.exam.entity.ExamPaper;
import com.exam.service.ExamPaperService;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.PaperVO;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 试卷管理控制器
 * 提供试卷的增删改查、发布、关闭等功能，教师和管理员可操作
 */
@RestController
@RequestMapping("/api/paper")
public class PaperController {

    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 分页查询试卷列表
     * 支持按学科、年级、状态筛选
     */
    @GetMapping("/list")
    public Result<IPage<ExamPaper>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String status) {
        return Result.success(examPaperService.pageList(pageNum, pageSize, subject, gradeLevel, status));
    }

    /**
     * 创建试卷
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ExamPaper> create(@RequestBody PaperDTO dto) {
        Long userId = getCurrentUserId();
        return Result.success(examPaperService.createPaper(dto, userId));
    }

    /**
     * 更新试卷信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<ExamPaper> update(@PathVariable Long id, @RequestBody PaperDTO dto) {
        dto.setId(id);
        return Result.success(examPaperService.updatePaper(dto));
    }

    /**
     * 删除试卷（级联删除关联题目，逻辑删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> delete(@PathVariable Long id) {
        examPaperService.deletePaper(id);
        return Result.success("删除成功",null);
    }

    /**
     * 发布试卷
     */
    @PostMapping("/publish/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> publish(@PathVariable Long id) {
        examPaperService.publishPaper(id);
        return Result.success("发布成功",null);
    }

    /**
     * 关闭试卷
     */
    @PostMapping("/close/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> close(@PathVariable Long id) {
        examPaperService.closePaper(id);
        return Result.success("关闭成功",null);
    }

    /**
     * 获取试卷详情（含题目信息）
     */
    @GetMapping("/{id}")
    public Result<PaperVO> detail(@PathVariable Long id) {
        return Result.success(examPaperService.getPaperDetail(id));
    }

    /**
     * 获取当前可参加的考试列表
     */
    @GetMapping("/available")
    public Result<List<ExamPaper>> available() {
        Long userId = getCurrentUserId();
        return Result.success(examPaperService.getAvailablePapers(userId));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }
}
