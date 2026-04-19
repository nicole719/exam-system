package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.QuestionDTO;
import com.exam.entity.QuestionBank;
import com.exam.vo.QuestionVO;

import java.util.List;

/**
 * 题库管理业务逻辑接口
 * 定义题目管理的核心业务操作，包括题目的创建、修改、删除、查询、自动选题等
 */
public interface QuestionBankService extends IService<QuestionBank> {
    /**
     * 分页查询题目列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param subject 学科
     * @param gradeLevel 年级
     * @param questionType 题目类型
     * @param difficulty 难度等级
     * @return 分页结果
     */
    IPage<QuestionBank> pageList(Integer pageNum, Integer pageSize, String subject, String gradeLevel, String questionType, Integer difficulty);

    /**
     * 创建题目
     * @param dto 题目信息
     * @param userId 创建人ID
     * @return 创建的题目对象
     */
    QuestionBank createQuestion(QuestionDTO dto, Long userId);

    /**
     * 更新题目信息
     * @param dto 题目信息（包含ID）
     * @return 更新后的题目对象
     */
    QuestionBank updateQuestion(QuestionDTO dto);

    /**
     * 删除题目（级联删除选项，逻辑删除）
     * @param id 题目ID
     */
    void deleteQuestion(Long id);

    /**
     * 获取题目详情（含选项信息）
     * @param id 题目ID
     * @return 题目详情VO
     */
    QuestionVO getQuestionDetail(Long id);

    /**
     * 根据ID列表批量获取题目
     * @param ids 题目ID列表
     * @return 题目列表
     */
    List<QuestionBank> getQuestionsByIds(List<Long> ids);

    /**
     * 自动选题生成
     * @param subject 学科
     * @param gradeLevel 年级
     * @param questionType 题目类型
     * @param count 题目数量
     * @param difficulty 难度等级（可选）
     * @return 随机选中的题目列表
     */
    List<QuestionBank> autoGenerateQuestions(String subject, String gradeLevel, String questionType, Integer count, Integer difficulty);
}
