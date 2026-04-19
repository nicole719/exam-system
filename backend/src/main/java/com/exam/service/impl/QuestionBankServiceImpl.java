package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.QuestionDTO;
import com.exam.entity.QuestionBank;
import com.exam.entity.QuestionOption;
import com.exam.mapper.QuestionBankMapper;
import com.exam.mapper.QuestionOptionMapper;
import com.exam.service.QuestionBankService;
import com.exam.vo.QuestionVO;
import com.exam.vo.QuestionVO.OptionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 题库管理业务逻辑实现类
 * 实现题目的创建、修改、删除、查询、自动选题等核心功能
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    /**
     * 分页查询题目列表
     * 支持按学科、年级、题型、难度进行筛选，按创建时间倒序排列
     */
    @Override
    public IPage<QuestionBank> pageList(Integer pageNum, Integer pageSize, String subject, String gradeLevel,
                                        String questionType, Integer difficulty) {
        Page<QuestionBank> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        if (subject != null && !subject.isEmpty()) {
            wrapper.eq(QuestionBank::getSubject, subject);
        }
        if (gradeLevel != null && !gradeLevel.isEmpty()) {
            wrapper.eq(QuestionBank::getGradeLevel, gradeLevel);
        }
        if (questionType != null && !questionType.isEmpty()) {
            wrapper.eq(QuestionBank::getQuestionType, questionType);
        }
        if (difficulty != null) {
            wrapper.eq(QuestionBank::getDifficulty, difficulty);
        }
        wrapper.orderByDesc(QuestionBank::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 创建题目
     * 1. 保存题目基本信息
     * 2. 如果存在选项，则保存选项列表
     */
    @Override
    @Transactional
    public QuestionBank createQuestion(QuestionDTO dto, Long userId) {
        QuestionBank question = new QuestionBank();
        BeanUtils.copyProperties(dto, question);
        question.setCreateUserId(userId);
        save(question);

        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            for (QuestionDTO.OptionDTO opt : dto.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel(opt.getOptionLabel());
                option.setOptionContent(opt.getOptionContent());
                option.setIsCorrect(opt.getIsCorrect());
                questionOptionMapper.insert(option);
            }
        }
        return question;
    }

    /**
     * 更新题目信息
     * 1. 更新题目基本信息
     * 2. 先删除原有选项，再插入新选项
     */
    @Override
    @Transactional
    public QuestionBank updateQuestion(QuestionDTO dto) {
        QuestionBank question = new QuestionBank();
        BeanUtils.copyProperties(dto, question);
        updateById(question);

        questionOptionMapper.delete(new LambdaQueryWrapper<QuestionOption>().eq(QuestionOption::getQuestionId, dto.getId()));
        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
            for (QuestionDTO.OptionDTO opt : dto.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setOptionLabel(opt.getOptionLabel());
                option.setOptionContent(opt.getOptionContent());
                option.setIsCorrect(opt.getIsCorrect());
                questionOptionMapper.insert(option);
            }
        }
        return question;
    }

    /**
     * 删除题目
     * 级联删除该题目的所有选项，然后逻辑删除题目
     */
    @Override
    public void deleteQuestion(Long id) {
        questionOptionMapper.delete(new LambdaQueryWrapper<QuestionOption>().eq(QuestionOption::getQuestionId, id));
        removeById(id);
    }

    /**
     * 获取题目详情（含选项信息）
     */
    @Override
    public QuestionVO getQuestionDetail(Long id) {
        QuestionBank question = getById(id);
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(question, vo);

        if (question.getDifficulty() != null) {
            String[] diffNames = {"", "简单", "中等", "困难"};
            vo.setDifficultyName(diffNames[question.getDifficulty()]);
        }

        List<QuestionOption> options = questionOptionMapper.selectList(
            new LambdaQueryWrapper<QuestionOption>().eq(QuestionOption::getQuestionId, id)
        );
        List<OptionVO> optionVOList = new ArrayList<>();
        for (QuestionOption opt : options) {
            OptionVO ov = new OptionVO();
            ov.setId(opt.getId());
            ov.setOptionLabel(opt.getOptionLabel());
            ov.setOptionContent(opt.getOptionContent());
            ov.setIsCorrect(opt.getIsCorrect());
            optionVOList.add(ov);
        }
        vo.setOptions(optionVOList);
        return vo;
    }

    /**
     * 根据ID列表批量获取题目
     */
    @Override
    public List<QuestionBank> getQuestionsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return list(new LambdaQueryWrapper<QuestionBank>().in(QuestionBank::getId, ids));
    }

    @Override
    public List<QuestionBank> autoGenerateQuestions(String subject, String gradeLevel, String questionType,
                                                     Integer count, Integer difficulty) {
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionBank::getSubject, subject);
        wrapper.eq(QuestionBank::getGradeLevel, gradeLevel);
        wrapper.eq(QuestionBank::getQuestionType, questionType);
        if (difficulty != null) {
            wrapper.eq(QuestionBank::getDifficulty, difficulty);
        }
        wrapper.last("LIMIT " + count);
        return list(wrapper);
    }
}
