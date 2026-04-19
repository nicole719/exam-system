package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.ExamAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试答题记录数据访问层
 * 提供对exam_answer表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface ExamAnswerMapper extends BaseMapper<ExamAnswer> {
}
