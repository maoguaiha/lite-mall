package com.macro.mall.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

/**
 * 会员登录参数
 */
@Data
public class UmsMemberLoginParam {
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
}
