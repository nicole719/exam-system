package com.exam.controller;

import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.entity.SysUser;
import com.exam.service.SysUserService;
import com.exam.utils.JwtUtils;
import com.exam.utils.UserDetailsImpl;
import com.exam.vo.LoginVO;
import com.exam.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * 认证控制器
 * 处理用户登录、注册、获取当前用户信息、登出等认证相关功能
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户登录
     * 使用Spring Security进行认证，认证成功后生成JWT令牌返回
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(dto.getUsername(), "", null);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        LoginVO vo = new LoginVO();
        vo.setToken(jwt);
        vo.setUserId(userDetails.getId());
        vo.setUsername(userDetails.getUsername());
        vo.setRealName(userDetails.getRealName());
        vo.setRole(userDetails.getRole());
        vo.setAvatar(userDetails.getAvatar());
        vo.setExpireTime(LocalDateTime.now().plusDays(7));

        return Result.success(vo);
    }

    /**
     * 用户注册
     * 注册时检查用户名是否已存在，默认注册为学生角色
     */
    @PostMapping("/register")
    public Result<SysUser> register(@RequestBody @Valid RegisterDTO dto) {
        if (sysUserService.checkUsername(dto.getUsername())) {
            return Result.error("用户名已存在");
        }
        SysUser user = sysUserService.register(dto);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/currentUser")
    public Result<UserDetailsImpl> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return Result.success((UserDetailsImpl) authentication.getPrincipal());
        }
        return Result.unauthorized("未登录");
    }

    /**
     * 用户登出
     * 清除SecurityContext中的认证信息
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("登出成功",null);
    }
}
