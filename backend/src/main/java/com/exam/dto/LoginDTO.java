package com.exam.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求数据传输对象
 * 用于接收用户登录请求的用户名和密码
 */
@Data
public class LoginDTO {
    /** 登录用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
