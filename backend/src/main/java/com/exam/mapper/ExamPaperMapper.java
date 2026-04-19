package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.ExamPaper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷数据访问层
 * 提供对exam_paper表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface ExamPaperMapper extends BaseMapper<ExamPaper> {
}
