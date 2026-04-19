package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.QuestionBank;
import com.exam.entity.WrongQuestion;
import com.exam.mapper.QuestionBankMapper;
import com.exam.mapper.WrongQuestionMapper;
import com.exam.service.WrongQuestionService;
import com.exam.service.QuestionBankService;
import com.exam.vo.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 错题本业务逻辑实现类
 * 实现错题记录、练习、移除等核心功能
 */
@Service
public class WrongQuestionServiceImpl extends ServiceImpl<WrongQuestionMapper, WrongQuestion> implements WrongQuestionService {

    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    /**
     * 分页查询错题列表
     */
    @Override
    public IPage<WrongQuestion> pageList(Integer pageNum, Integer pageSize, Long userId) {
        Page<WrongQuestion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getUserId, userId);
        wrapper.orderByDesc(WrongQuestion::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 记录错题
     * 如果已存在则累加错误次数，否则新建记录
     */
    @Override
    public void recordWrong(Long userId, Long questionId, String wrongReason) {
        WrongQuestion wrong = getOne(
            new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)
                .eq(WrongQuestion::getQuestionId, questionId)
        );
        if (wrong == null) {
            wrong = new WrongQuestion();
            wrong.setUserId(userId);
            wrong.setQuestionId(questionId);
            wrong.setWrongCount(1);
            wrong.setWrongReason(wrongReason);
            save(wrong);
        } else {
            wrong.setWrongCount(wrong.getWrongCount() + 1);
            wrong.setLastPracticeTime(LocalDateTime.now());
            wrong.setWrongReason(wrongReason);
            updateById(wrong);
        }
    }

    @Override
    public void practiceQuestion(Long id, Long userId) {
        WrongQuestion wrong = getOne(
            new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getId, id)
                .eq(WrongQuestion::getUserId, userId)
        );
        if (wrong != null) {
            wrong.setLastPracticeTime(LocalDateTime.now());
            updateById(wrong);
        }
    }

    @Override
    public List<QuestionVO> getWrongQuestions(Long userId) {
        List<WrongQuestion> wrongList = list(
            new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getUserId, userId)
        );
        return wrongList.stream().map(w -> {
            QuestionVO vo = questionBankService.getQuestionDetail(w.getQuestionId());
            if (vo != null) {
                BeanUtils.copyProperties(w, vo);
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void removeFromWrongBook(Long id, Long userId) {
        remove(
            new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getId, id)
                .eq(WrongQuestion::getUserId, userId)
        );
    }
}
