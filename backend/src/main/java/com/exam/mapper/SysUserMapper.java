package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户数据访问层
 * 提供对sys_user表的数据库操作，基于MyBatis-Plus实现CRUD功能
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
