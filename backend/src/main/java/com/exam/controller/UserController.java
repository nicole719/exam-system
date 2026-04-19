package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.entity.SysUser;
import com.exam.service.SysUserService;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 提供用户的增删改查、状态管理等功能，仅管理员可访问
 */
@RestController

@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表
     * 支持按用户名（模糊匹配）和角色筛选，需要管理员或教师权限
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<IPage<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        return Result.success(sysUserService.pageList(pageNum, pageSize, username, role));
    }

    /**
     * 创建用户（管理员操作）
     * 创建时检查用户名是否已存在
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> create(@RequestBody SysUser user) {
        if (sysUserService.checkUsername(user.getUsername())) {
            return Result.error("用户名已存在");
        }
        SysUser result = sysUserService.register(
            com.exam.dto.RegisterDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .gender(user.getGender())
                .build()
        );
        result.setRole(user.getRole());
        return Result.success(result);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        SysUser result = sysUserService.updateUser(user);
        result.setPassword(null);
        return Result.success(result);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success("删除成功",null);
    }

    /**
     * 修改用户状态
     * @param status 1-正常，0-禁用
     */
    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        sysUserService.changeStatus(id, status);
        return Result.success("状态修改成功",null);
    }
}
