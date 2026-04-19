package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试记录数据访问层
 * 提供对exam_record表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
}
