package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错题记录数据访问层
 * 提供对wrong_question表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {
}
