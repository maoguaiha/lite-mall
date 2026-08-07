package com.macro.mall.admin.dto;

import java.util.List;

/**
 * 当前登录管理员信息（含角色编码）
 */
public class AdminInfo {

    private String username;
    private List<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
