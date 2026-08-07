package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsAdminRoleAdminMapper;
import com.macro.mall.mapper.UmsMenuAdminMapper;
import com.macro.mall.mapper.UmsRoleAdminMapper;
import com.macro.mall.mapper.UmsRoleMenuAdminMapper;
import com.macro.mall.model.UmsAdminRole;
import com.macro.mall.model.UmsMenu;
import com.macro.mall.model.UmsRole;
import com.macro.mall.model.UmsRoleMenu;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RoleAdminServiceImpl {

    private final UmsRoleAdminMapper roleMapper;
    private final UmsAdminRoleAdminMapper adminRoleMapper;
    private final UmsRoleMenuAdminMapper roleMenuMapper;
    private final UmsMenuAdminMapper menuMapper;

    public RoleAdminServiceImpl(UmsRoleAdminMapper roleMapper,
                                UmsAdminRoleAdminMapper adminRoleMapper,
                                UmsRoleMenuAdminMapper roleMenuMapper,
                                UmsMenuAdminMapper menuMapper) {
        this.roleMapper = roleMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.menuMapper = menuMapper;
    }

    public CommonPage<UmsRole> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<UmsRole> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        qw.eq("delete_flag", 0).orderByAsc("sort");
        return CommonPage.restPage(roleMapper.selectList(qw));
    }

    public void create(UmsRole role) {
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getDeleteFlag() == null) {
            role.setDeleteFlag(0);
        }
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleMapper.insert(role);
    }

    public void update(UmsRole role) {
        if (role.getId() == null) {
            Asserts.fail("角色ID不能为空");
        }
        role.setUpdateTime(new Date());
        roleMapper.updateById(role);
    }

    public void delete(Long id) {
        List<UmsAdminRole> adminRoles = adminRoleMapper.selectList(new QueryWrapper<UmsAdminRole>().eq("role_id", id));
        for (UmsAdminRole ar : adminRoles) {
            adminRoleMapper.deleteById(ar.getId());
        }
        List<UmsRoleMenu> roleMenus = roleMenuMapper.selectList(new QueryWrapper<UmsRoleMenu>().eq("role_id", id));
        for (UmsRoleMenu rm : roleMenus) {
            roleMenuMapper.deleteById(rm.getId());
        }
        roleMapper.deleteById(id);
    }

    public List<UmsMenu> menus(Long roleId) {
        List<UmsRoleMenu> links = roleMenuMapper.selectList(new QueryWrapper<UmsRoleMenu>().eq("role_id", roleId));
        List<UmsMenu> result = new ArrayList<>();
        for (UmsRoleMenu link : links) {
            UmsMenu menu = menuMapper.selectById(link.getMenuId());
            if (menu != null) {
                result.add(menu);
            }
        }
        return result;
    }
}
