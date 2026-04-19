package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.RegisterDTO;
import com.exam.entity.SysUser;

/**
 * 系统用户业务逻辑接口
 * 定义用户管理的核心业务操作，包括用户分页查询、注册、修改、删除、状态变更等
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param username 用户名（模糊匹配）
     * @param role 用户角色
     * @return 分页结果
     */
    IPage<SysUser> pageList(Integer pageNum, Integer pageSize, String username, String role);

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 创建的用户对象
     */
    SysUser register(RegisterDTO dto);

    /**
     * 更新用户信息
     * @param user 用户信息（包含ID）
     * @return 更新后的用户对象
     */
    SysUser updateUser(SysUser user);

    /**
     * 删除用户（逻辑删除）
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 修改用户状态
     * @param id 用户ID
     * @param status 状态值：1-正常，0-禁用
     */
    void changeStatus(Long id, Integer status);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true-已存在，false-不存在
     */
    boolean checkUsername(String username);
}
