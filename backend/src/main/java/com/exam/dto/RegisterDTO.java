package com.exam.dto;

import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户注册请求数据传输对象
 * 用于接收用户注册请求的完整信息
 */
@Data
@Builder
public class RegisterDTO {
    /** 登录用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    /** 联系电话 */
    private String phone;
    /** 电子邮箱 */
    private String email;
    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;
    /** 年级 */
    private String gradeLevel;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}
