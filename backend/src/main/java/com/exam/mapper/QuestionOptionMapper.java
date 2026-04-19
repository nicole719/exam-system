package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.QuestionOption;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目选项数据访问层
 * 提供对question_option表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface QuestionOptionMapper extends BaseMapper<QuestionOption> {
}
