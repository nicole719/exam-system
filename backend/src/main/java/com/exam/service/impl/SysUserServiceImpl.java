package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.RegisterDTO;
import com.exam.entity.SysUser;
import com.exam.mapper.SysUserMapper;
import com.exam.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 系统用户业务逻辑实现类
 * 实现用户管理的核心功能，包括注册、修改、删除、状态变更等
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表，支持按用户名和角色筛选
     * 按创建时间倒序排列
     */
    @Override
    public IPage<SysUser> pageList(Integer pageNum, Integer pageSize, String username, String role) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 用户注册
     * 1. 将密码进行BCrypt加密
     * 2. 设置默认角色为STUDENT
     * 3. 设置状态为正常
     */
    @Override
    public SysUser register(RegisterDTO dto) {
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setRole("STUDENT");
        user.setStatus(1);
        save(user);
        return user;
    }

    /**
     * 更新用户信息
     * 如果提供了新密码，则进行加密后再更新
     */
    @Override
    public SysUser updateUser(SysUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        updateById(user);
        return getById(user.getId());
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Override
    public void deleteUser(Long id) {
        removeById(id);
    }

    /**
     * 修改用户状态
     * @param status 1-正常，0-禁用
     */
    @Override
    public void changeStatus(Long id, Integer status) {
        SysUser user = getById(id);
        if (user != null) {
            user.setStatus(status);
            updateById(user);
        }
    }

    /**
     * 检查用户名是否已被使用
     */
    @Override
    public boolean checkUsername(String username) {
        return count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0;
    }
}
