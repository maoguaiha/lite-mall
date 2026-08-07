package com.macro.mall.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

/**
 * 会员注册参数
 */
@Data
public class UmsMemberParam {
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    private String phone;
    private String nickname;
    private String icon;
}
