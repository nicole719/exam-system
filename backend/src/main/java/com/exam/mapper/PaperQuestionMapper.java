package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.PaperQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷题目关联数据访问层
 * 提供对paper_question表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {
}
