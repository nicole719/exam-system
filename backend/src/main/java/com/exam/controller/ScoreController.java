package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.service.ExamRecordService;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.ExamRecordVO;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 成绩管理控制器
 * 提供考试记录查询、成绩详情查看等功能
 */
@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ExamRecordService examRecordService;

    /**
     * 分页查询考试记录/成绩列表
     */
    @GetMapping("/list")
    public Result<IPage<ExamRecordVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getCurrentUserId();
        String role = getCurrentUserRole();
        return Result.success(examRecordService.pageList(pageNum, pageSize, userId, role));
    }

    /**
     * 获取成绩详情
     * 学生只能查看自己的成绩
     */
    @GetMapping("/detail/{recordId}")
    public Result<ExamRecordVO> detail(@PathVariable Long recordId) {
        Long userId = getCurrentUserId();
        String role = getCurrentUserRole();
        if ("STUDENT".equals(role)) {
            return Result.success(examRecordService.getMyRecordDetail(recordId, userId));
        }
        return Result.success(examRecordService.getRecordDetail(recordId));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getRole();
        }
        return "";
    }
}
