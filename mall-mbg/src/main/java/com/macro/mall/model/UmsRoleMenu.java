package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色-菜单 关联
 */
@Data
public class UmsRoleMenu implements Serializable {
    private Long id;
    private Long roleId;
    private Long menuId;
}
