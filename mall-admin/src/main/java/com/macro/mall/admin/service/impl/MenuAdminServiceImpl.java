package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMenuAdminMapper;
import com.macro.mall.mapper.UmsRoleMenuAdminMapper;
import com.macro.mall.model.UmsMenu;
import com.macro.mall.model.UmsRoleMenu;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MenuAdminServiceImpl {

    private final UmsMenuAdminMapper menuMapper;
    private final UmsRoleMenuAdminMapper roleMenuMapper;

    public MenuAdminServiceImpl(UmsMenuAdminMapper menuMapper, UmsRoleMenuAdminMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    public List<UmsMenu> listAll() {
        QueryWrapper<UmsMenu> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        return menuMapper.selectList(qw);
    }

    public void create(UmsMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        menu.setCreateTime(new Date());
        menu.setUpdateTime(new Date());
        menuMapper.insert(menu);
    }

    public void update(UmsMenu menu) {
        if (menu.getId() == null) {
            Asserts.fail("菜单ID不能为空");
        }
        menu.setUpdateTime(new Date());
        menuMapper.updateById(menu);
    }

    public void delete(Long id) {
        List<UmsRoleMenu> links = roleMenuMapper.selectList(new QueryWrapper<UmsRoleMenu>().eq("menu_id", id));
        for (UmsRoleMenu link : links) {
            roleMenuMapper.deleteById(link.getId());
        }
        menuMapper.deleteById(id);
    }
}
