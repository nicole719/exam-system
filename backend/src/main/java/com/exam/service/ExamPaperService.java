package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.PaperDTO;
import com.exam.entity.ExamPaper;
import com.exam.vo.ExamRecordVO;
import com.exam.vo.PaperVO;

import java.util.List;

/**
 * 试卷管理业务逻辑接口
 * 定义试卷管理的核心业务操作，包括创建、修改、删除、发布、关闭等
 */
public interface ExamPaperService extends IService<ExamPaper> {
    /**
     * 分页查询试卷列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param subject 学科
     * @param gradeLevel 年级
     * @param status 试卷状态
     * @return 分页结果
     */
    IPage<ExamPaper> pageList(Integer pageNum, Integer pageSize, String subject, String gradeLevel, String status);

    /**
     * 创建试卷
     * @param dto 试卷信息
     * @param userId 创建人ID
     * @return 创建的试卷对象
     */
    ExamPaper createPaper(PaperDTO dto, Long userId);

    /**
     * 更新试卷信息
     * @param dto 试卷信息（包含ID）
     * @return 更新后的试卷对象
     */
    ExamPaper updatePaper(PaperDTO dto);

    /**
     * 删除试卷（级联删除关联题目，逻辑删除）
     * @param id 试卷ID
     */
    void deletePaper(Long id);

    /**
     * 发布试卷
     * @param id 试卷ID
     */
    void publishPaper(Long id);

    /**
     * 关闭试卷
     * @param id 试卷ID
     */
    void closePaper(Long id);

    /**
     * 获取试卷详情（含题目信息）
     * @param id 试卷ID
     * @return 试卷详情VO
     */
    PaperVO getPaperDetail(Long id);

    /**
     * 获取考试用试卷（隐藏答案和解析）
     * @param id 试卷ID
     * @return 隐藏敏感信息的试卷VO
     */
    PaperVO getPaperForExam(Long id);

    /**
     * 获取当前可参加的考试列表
     * @param userId 用户ID
     * @return 可参加的试卷列表（状态为已发布且在有效期内）
     */
    List<ExamPaper> getAvailablePapers(Long userId);
}
