package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.entity.WrongQuestion;
import com.exam.service.WrongQuestionService;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.QuestionVO;
import com.exam.vo.Result;
import com.exam.vo.WrongQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 错题本控制器
 * 提供错题记录、查看、练习、移除等功能
 */
@RestController
@RequestMapping("/api/wrong")
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;

    /**
     * 分页查询错题列表
     */
    @GetMapping("/list")
    public Result<IPage<WrongQuestionVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getCurrentUserId();
        return Result.success(wrongQuestionService.pageList(pageNum, pageSize, userId));
    }

    /**
     * 获取错题列表（含题目详情）
     */
    @GetMapping("/questions")
    public Result<List<QuestionVO>> questions() {
        Long userId = getCurrentUserId();
        return Result.success(wrongQuestionService.getWrongQuestions(userId));
    }

    /**
     * 练习错题（更新最后一次练习时间）
     */
    @PostMapping("/practice/{id}")
    public Result<Void> practice(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        wrongQuestionService.practiceQuestion(id, userId);
        return Result.success("练习记录成功",null);
    }

    /**
     * 记录错题
     */
    @PostMapping("/record")
    public Result<Void> record(@RequestBody WrongQuestion wrong) {
        Long userId = getCurrentUserId();
        wrongQuestionService.recordWrong(userId, wrong.getQuestionId(), wrong.getWrongReason());
        return Result.success("记录成功",null);
    }

    /**
     * 从错题本移除
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        wrongQuestionService.removeFromWrongBook(id, userId);
        return Result.success("移除成功",null);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }
}
