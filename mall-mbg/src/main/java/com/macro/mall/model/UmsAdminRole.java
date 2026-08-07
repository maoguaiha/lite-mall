package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员-角色 关联
 */
@Data
public class UmsAdminRole implements Serializable {
    private Long id;
    private Long adminId;
    private Long roleId;
}
