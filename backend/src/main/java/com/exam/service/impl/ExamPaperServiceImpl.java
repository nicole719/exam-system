package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.PaperDTO;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ExamPaperService;
import com.exam.service.QuestionBankService;
import com.exam.vo.PaperVO;
import com.exam.vo.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 试卷管理业务逻辑实现类
 * 实现试卷的创建、修改、删除、发布、关闭及详情获取等核心功能
 */
@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamPaperService {

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private QuestionBankService questionBankService;

    /**
     * 分页查询试卷列表
     * 支持按学科、年级、状态筛选，按创建时间倒序排列
     */
    @Override
    public IPage<ExamPaper> pageList(Integer pageNum, Integer pageSize, String subject, String gradeLevel, String status) {
        Page<ExamPaper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        if (subject != null && !subject.isEmpty()) {
            wrapper.eq(ExamPaper::getSubject, subject);
        }
        if (gradeLevel != null && !gradeLevel.isEmpty()) {
            wrapper.eq(ExamPaper::getGradeLevel, gradeLevel);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ExamPaper::getStatus, status);
        }
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 创建试卷
     * 1. 保存试卷基本信息，状态默认为草稿
     * 2. 建立试卷与题目的关联关系
     * 3. 自动计算总分
     */
    @Override
    @Transactional
    public ExamPaper createPaper(PaperDTO dto, Long userId) {
        ExamPaper paper = new ExamPaper();
        BeanUtils.copyProperties(dto, paper);
        paper.setCreateUserId(userId);
        paper.setStatus("DRAFT");
        save(paper);

        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            int totalScore = 0;
            int sortOrder = 1;
            for (Long qId : dto.getQuestionIds()) {
                QuestionBank q = questionBankMapper.selectById(qId);
                PaperQuestion pq = new PaperQuestion();
                pq.setPaperId(paper.getId());
                pq.setQuestionId(qId);
                pq.setSortOrder(sortOrder++);
                int score = q != null ? q.getScore() : 5;
                pq.setScore(score);
                totalScore += score;
                paperQuestionMapper.insert(pq);
            }
            paper.setTotalScore(totalScore);
            updateById(paper);
        }
        return paper;
    }

    /**
     * 更新试卷信息
     * 1. 更新基本信息
     * 2. 重新建立试卷与题目的关联
     */
    @Override
    @Transactional
    public ExamPaper updatePaper(PaperDTO dto) {
        ExamPaper paper = new ExamPaper();
        BeanUtils.copyProperties(dto, paper);
        updateById(paper);

        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, dto.getId()));
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            int totalScore = 0;
            int sortOrder = 1;
            for (Long qId : dto.getQuestionIds()) {
                QuestionBank q = questionBankMapper.selectById(qId);
                PaperQuestion pq = new PaperQuestion();
                pq.setPaperId(paper.getId());
                pq.setQuestionId(qId);
                pq.setSortOrder(sortOrder++);
                int score = q != null ? q.getScore() : 5;
                pq.setScore(score);
                totalScore += score;
                paperQuestionMapper.insert(pq);
            }
            paper.setTotalScore(totalScore);
            updateById(paper);
        }
        return paper;
    }

    /**
     * 删除试卷
     * 级联删除关联的题目，然后逻辑删除试卷
     */
    @Override
    public void deletePaper(Long id) {
        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, id));
        removeById(id);
    }

    /**
     * 发布试卷
     * 将试卷状态设置为已发布
     */
    @Override
    public void publishPaper(Long id) {
        ExamPaper paper = getById(id);
        if (paper != null) {
            paper.setStatus("PUBLISHED");
            updateById(paper);
        }
    }

    /**
     * 关闭试卷
     * 将试卷状态设置为已关闭
     */
    @Override
    public void closePaper(Long id) {
        ExamPaper paper = getById(id);
        if (paper != null) {
            paper.setStatus("CLOSED");
            updateById(paper);
        }
    }

    /**
     * 获取试卷详情（含题目信息）
     */
    @Override
    public PaperVO getPaperDetail(Long id) {
        ExamPaper paper = getById(id);
        PaperVO vo = new PaperVO();
        BeanUtils.copyProperties(paper, vo);
        vo.setQuestions(getQuestionVOs(id));
        return vo;
    }

    /**
     * 获取考试用试卷（隐藏答案和解析）
     * 用于学生参加考试时获取试卷，隐藏正确选项、答案和解析
     */
    @Override
    public PaperVO getPaperForExam(Long id) {
        ExamPaper paper = getById(id);
        PaperVO vo = new PaperVO();
        BeanUtils.copyProperties(paper, vo);
        List<QuestionVO> questions = getQuestionVOs(id);
        for (QuestionVO q : questions) {
            q.setAnswer(null);
            q.setAnalysis(null);
            if (q.getOptions() != null) {
                for (QuestionVO.OptionVO opt : q.getOptions()) {
                    opt.setIsCorrect(null);
                }
            }
        }
        vo.setQuestions(questions);
        return vo;
    }

    /**
     * 获取试卷的题目列表
     */
    private List<QuestionVO> getQuestionVOs(Long paperId) {
        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
            new LambdaQueryWrapper<PaperQuestion>()
                .eq(PaperQuestion::getPaperId, paperId)
                .orderByAsc(PaperQuestion::getSortOrder)
        );
        List<QuestionVO> result = new ArrayList<>();
        for (PaperQuestion pq : pqs) {
            QuestionVO qvo = questionBankService.getQuestionDetail(pq.getQuestionId());
            if (qvo != null) {
                result.add(qvo);
            }
        }
        return result;
    }

    /**
     * 获取当前可参加的考试列表
     * 条件：状态为已发布且未过期
     */
    @Override
    public List<ExamPaper> getAvailablePapers(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPaper::getStatus, "PUBLISHED");
        wrapper.and(w -> w.isNull(ExamPaper::getEndTime).or().ge(ExamPaper::getEndTime, now));
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        return list(wrapper);
    }
}
