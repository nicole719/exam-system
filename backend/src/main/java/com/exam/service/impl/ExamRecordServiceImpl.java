package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.ExamStartDTO;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ExamRecordService;
import com.exam.service.ExamPaperService;
import com.exam.vo.ExamRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 考试记录业务逻辑实现类
 * 实现考试过程的核心功能，包括开始考试、保存答案、提交试卷、自动记录错题等
 */
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private ExamPaperService examPaperService;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private WrongQuestionMapper wrongQuestionMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${exam.answer-cache-prefix}")
    private String cachePrefix;

    @Value("${exam.answer-cache-ttl}")
    private Long cacheTtl;

    /**
     * 开始考试
     * 1. 校验试卷是否存在且在有效期内
     * 2. 创建考试记录
     * 3. 在Redis中缓存考试状态
     */
    @Override
    @Transactional
    public ExamRecord startExam(ExamStartDTO dto, Long userId) {
        ExamPaper paper = examPaperService.getById(dto.getPaperId());
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        if (paper.getStartTime() != null && now.isBefore(paper.getStartTime())) {
            throw new RuntimeException("考试尚未开始");
        }
        if (paper.getEndTime() != null && now.isAfter(paper.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }

        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setPaperId(dto.getPaperId());
        record.setStatus("ING");
        record.setStartTime(now);
        record.setIpAddress(dto.getIpAddress());
        record.setScore(0.0);
        record.setObjectiveScore(0.0);
        record.setSubjectiveScore(0.0);
        save(record);

        String cacheKey = cachePrefix + record.getId();
        redisTemplate.opsForHash().put(cacheKey, "status", "ING");
        redisTemplate.expire(cacheKey, cacheTtl, TimeUnit.SECONDS);

        return record;
    }

    /**
     * 保存答题进度
     * 同时保存到Redis（临时缓存）和数据库（持久化）
     */
    @Override
    public void saveAnswer(Long recordId, Long questionId, String userAnswer) {
        String cacheKey = cachePrefix + recordId;
        redisTemplate.opsForHash().put(cacheKey, "q_" + questionId, userAnswer);
        redisTemplate.expire(cacheKey, cacheTtl, TimeUnit.SECONDS);

        ExamAnswer answer = examAnswerMapper.selectOne(
            new LambdaQueryWrapper<ExamAnswer>()
                .eq(ExamAnswer::getRecordId, recordId)
                .eq(ExamAnswer::getQuestionId, questionId)
        );
        if (answer == null) {
            answer = new ExamAnswer();
            answer.setRecordId(recordId);
            answer.setQuestionId(questionId);
            answer.setUserAnswer(userAnswer);
            examAnswerMapper.insert(answer);
        } else {
            answer.setUserAnswer(userAnswer);
            examAnswerMapper.updateById(answer);
        }
    }

        /**
     * 获取答题进度
     * 从Redis缓存中读取答题进度
     */
    @Override
    public Map<Long, String> getAnswerProgress(Long recordId) {
        String cacheKey = cachePrefix + recordId;
        Map<Object, Object> cached = redisTemplate.opsForHash().entries(cacheKey);
        Map<Long, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : cached.entrySet()) {
            String key = (String) entry.getKey();
            if (key.startsWith("q_")) {
                Long qId = Long.parseLong(key.substring(2));
                result.put(qId, (String) entry.getValue());
            }
        }
        return result;
    }

    /**
     * 提交试卷
     * 1. 获取所有答题记录
     * 2. 自动评判客观题
     * 3. 错题自动记录到错题本
     * 4. 计算总分并更新状态
     * 5. 清除Redis缓存
     */
    @Override
    @Transactional
    public ExamRecord submitExam(Long recordId) {
        ExamRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }

        String cacheKey = cachePrefix + recordId;
        Map<Object, Object> cached = redisTemplate.opsForHash().entries(cacheKey);

        List<ExamAnswer> answers = examAnswerMapper.selectList(
            new LambdaQueryWrapper<ExamAnswer>().eq(ExamAnswer::getRecordId, recordId)
        );

        double objectiveScore = 0.0;
        double subjectiveScore = 0.0;

        for (ExamAnswer answer : answers) {
            QuestionBank question = questionBankMapper.selectById(answer.getQuestionId());
            if (question == null) continue;

            String correctAnswer = question.getAnswer();
            String userAnswer = answer.getUserAnswer();

            if ("简答题".equals(question.getQuestionType()) || "填空题".equals(question.getQuestionType())) {
                subjectiveScore += answer.getScore() != null ? answer.getScore() : 0;
            } else {
                boolean isCorrect = correctAnswer != null && correctAnswer.equalsIgnoreCase(userAnswer);
                answer.setIsCorrect(isCorrect);
                if (isCorrect) {
                    objectiveScore += question.getScore();
                } else {
                    addToWrongBook(record.getUserId(), answer.getQuestionId());
                }
                examAnswerMapper.updateById(answer);
            }
        }

        record.setObjectiveScore(objectiveScore);
        record.setSubjectiveScore(subjectiveScore);
        record.setScore(objectiveScore + subjectiveScore);
        record.setStatus("SUBMITTED");
        record.setSubmitTime(LocalDateTime.now());
        updateById(record);

        redisTemplate.delete(cacheKey);
        return record;
    }

    /**
     * 添加错题到错题本
     * 如果已存在则累加错误次数，否则新建记录
     */
    private void addToWrongBook(Long userId, Long questionId) {
        WrongQuestion wrong = wrongQuestionMapper.selectOne(
            new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)
                .eq(WrongQuestion::getQuestionId, questionId)
        );
        if (wrong == null) {
            wrong = new WrongQuestion();
            wrong.setUserId(userId);
            wrong.setQuestionId(questionId);
            wrong.setWrongCount(1);
            wrongQuestionMapper.insert(wrong);
        } else {
            wrong.setWrongCount(wrong.getWrongCount() + 1);
            wrong.setLastPracticeTime(LocalDateTime.now());
            wrongQuestionMapper.updateById(wrong);
        }
    }

        /**
     * 分页查询考试记录
     * 学生只能查看自己的记录，教师可以查看所有记录
     */
    @Override
    public IPage<ExamRecordVO> pageList(Integer pageNum, Integer pageSize, Long userId, String role) {
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        if ("STUDENT".equals(role)) {
            wrapper.eq(ExamRecord::getUserId, userId);
        }
        wrapper.orderByDesc(ExamRecord::getCreateTime);
        IPage<ExamRecord> recordPage = page(page, wrapper);

        IPage<ExamRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<ExamRecordVO> voList = recordPage.getRecords().stream().map(r -> {
            ExamRecordVO vo = new ExamRecordVO();
            BeanUtils.copyProperties(r, vo);
            ExamPaper paper = examPaperService.getById(r.getPaperId());
            if (paper != null) {
                vo.setPaperTitle(paper.getTitle());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取考试记录详情（教师查看）
     */
    @Override
    public ExamRecordVO getRecordDetail(Long recordId) {
        ExamRecord record = getById(recordId);
        ExamRecordVO vo = new ExamRecordVO();
        if (record != null) {
            BeanUtils.copyProperties(record, vo);
            ExamPaper paper = examPaperService.getById(record.getPaperId());
            if (paper != null) {
                vo.setPaperTitle(paper.getTitle());
            }
            List<ExamAnswer> answers = examAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamAnswer>().eq(ExamAnswer::getRecordId, recordId)
            );
            vo.setTotalQuestions(answers.size());
            vo.setCorrectCount((int) answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count());
            vo.setWrongCount(vo.getTotalQuestions() - vo.getCorrectCount());
        }
        return vo;
    }

    /**
     * 获取我的考试记录详情（学生只能查看自己的）
     */
    @Override
    public ExamRecordVO getMyRecordDetail(Long recordId, Long userId) {
        ExamRecord record = getOne(
            new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getId, recordId)
                .eq(ExamRecord::getUserId, userId)
        );
        return getRecordDetail(record != null ? recordId : 0L);
    }
}
