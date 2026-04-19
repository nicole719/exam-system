package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.ExamStartDTO;
import com.exam.entity.ExamRecord;
import com.exam.vo.ExamRecordVO;

import java.util.Map;

/**
 * 考试记录业务逻辑接口
 * 定义考试过程的核心业务操作，包括开始考试、保存答案、提交试卷、查询成绩等
 */
public interface ExamRecordService extends IService<ExamRecord> {
    /**
     * 开始考试
     * @param dto 考试开始信息（包含试卷ID、IP地址等）
     * @param userId 用户ID
     * @return 创建的考试记录
     */
    ExamRecord startExam(ExamStartDTO dto, Long userId);

    /**
     * 保存答题进度
     * @param recordId 考试记录ID
     * @param questionId 题目ID
     * @param userAnswer 用户答案
     */
    void saveAnswer(Long recordId, Long questionId, String userAnswer);

    /**
     * 获取答题进度
     * @param recordId 考试记录ID
     * @return 题目ID到答案的映射
     */
    Map<Long, String> getAnswerProgress(Long recordId);

    /**
     * 提交试卷
     * @param recordId 考试记录ID
     * @return 更新后的考试记录
     */
    ExamRecord submitExam(Long recordId);

    /**
     * 分页查询考试记录
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户ID（学生模式下使用）
     * @param role 用户角色
     * @return 分页结果
     */
    IPage<ExamRecordVO> pageList(Integer pageNum, Integer pageSize, Long userId, String role);

    /**
     * 获取考试记录详情（教师查看）
     * @param recordId 考试记录ID
     * @return 考试记录详情VO
     */
    ExamRecordVO getRecordDetail(Long recordId);

    /**
     * 获取我的考试记录详情（学生只能查看自己的）
     * @param recordId 考试记录ID
     * @param userId 用户ID
     * @return 考试记录详情VO
     */
    ExamRecordVO getMyRecordDetail(Long recordId, Long userId);
}
