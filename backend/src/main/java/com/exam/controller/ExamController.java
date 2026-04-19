package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.dto.ExamStartDTO;
import com.exam.entity.ExamRecord;
import com.exam.service.ExamRecordService;
import com.exam.service.ExamPaperService;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.ExamRecordVO;
import com.exam.vo.PaperVO;
import com.exam.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 考试控制器
 * 处理考试的核心流程，包括获取试卷信息、开始考试、保存答案、提交试卷、查看成绩等
 */
@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ExamRecordService examRecordService;

    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 获取考试试卷信息（学生考试用，隐藏答案和解析）
     */
    @GetMapping("/info/{paperId}")
    public Result<PaperVO> getExamInfo(@PathVariable Long paperId) {
        return Result.success(examPaperService.getPaperForExam(paperId));
    }

    /**
     * 开始考试
     * 校验试卷有效性，创建考试记录
     */
    @PostMapping("/start/{paperId}")
    public Result<ExamRecord> startExam(@PathVariable Long paperId, HttpServletRequest request) {
        Long userId = getCurrentUserId();
        ExamStartDTO dto = new ExamStartDTO();
        dto.setPaperId(paperId);
        dto.setIpAddress(request.getRemoteAddr());
        return Result.success(examRecordService.startExam(dto, userId));
    }

    /**
     * 保存答题进度
     */
    @PostMapping("/saveAnswer")
    public Result<Void> saveAnswer(@RequestBody Map<String, Object> params) {
        Long recordId = Long.valueOf(params.get("recordId").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());
        String userAnswer = params.get("userAnswer") != null ? params.get("userAnswer").toString() : "";
        examRecordService.saveAnswer(recordId, questionId, userAnswer);
        return Result.success("保存成功",null);
    }

    /**
     * 获取答题进度
     */
    @GetMapping("/progress/{recordId}")
    public Result<Map<Long, String>> getProgress(@PathVariable Long recordId) {
        return Result.success(examRecordService.getAnswerProgress(recordId));
    }

    /**
     * 提交试卷
     * 自动评判客观题，错题记录到错题本
     */
    @PostMapping("/submit/{recordId}")
    public Result<ExamRecord> submitExam(@PathVariable Long recordId) {
        return Result.success(examRecordService.submitExam(recordId));
    }

    @GetMapping("/history")
    public Result<IPage<ExamRecordVO>> history(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getCurrentUserId();
        String role = getCurrentUserRole();
        return Result.success(examRecordService.pageList(pageNum, pageSize, userId, role));
    }

    /**
     * 获取考试记录详情
     * 学生只能查看自己的记录，教师可以查看所有记录
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
