package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.WrongQuestion;
import com.exam.vo.QuestionVO;

import java.util.List;

/**
 * 错题本业务逻辑接口
 * 定义错题管理的核心业务操作，包括记录错题、练习、移除等
 */
public interface WrongQuestionService extends IService<WrongQuestion> {
    /**
     * 分页查询错题列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户ID
     * @return 错题分页结果
     */
    IPage<WrongQuestion> pageList(Integer pageNum, Integer pageSize, Long userId);

    /**
     * 记录错题
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param wrongReason 错误原因
     */
    void recordWrong(Long userId, Long questionId, String wrongReason);

    /**
     * 练习错题（更新最后一次练习时间）
     * @param id 错题记录ID
     * @param userId 用户ID
     */
    void practiceQuestion(Long id, Long userId);

    /**
     * 获取用户的错题列表（含题目详情）
     * @param userId 用户ID
     * @return 错题详情列表
     */
    List<QuestionVO> getWrongQuestions(Long userId);

    /**
     * 从错题本移除
     * @param id 错题记录ID
     * @param userId 用户ID
     */
    void removeFromWrongBook(Long id, Long userId);
}
