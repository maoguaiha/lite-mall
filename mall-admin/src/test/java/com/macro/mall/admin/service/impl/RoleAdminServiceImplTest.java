package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.UmsAdminRoleAdminMapper;
import com.macro.mall.mapper.UmsMenuAdminMapper;
import com.macro.mall.mapper.UmsRoleAdminMapper;
import com.macro.mall.mapper.UmsRoleMenuAdminMapper;
import com.macro.mall.model.UmsAdminRole;
import com.macro.mall.model.UmsRole;
import com.macro.mall.model.UmsRoleMenu;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleAdminServiceImplTest {

    @Mock
    UmsRoleAdminMapper roleMapper;
    @Mock
    UmsAdminRoleAdminMapper adminRoleMapper;
    @Mock
    UmsRoleMenuAdminMapper roleMenuMapper;
    @Mock
    UmsMenuAdminMapper menuMapper;
    @InjectMocks
    RoleAdminServiceImpl service;

    @Test
    void delete_cascadesAssociations() {
        UmsAdminRole ar = new UmsAdminRole();
        ar.setId(10L);
        UmsRoleMenu rm = new UmsRoleMenu();
        rm.setId(20L);
        when(adminRoleMapper.selectList(any())).thenReturn(List.of(ar));
        when(roleMenuMapper.selectList(any())).thenReturn(List.of(rm));

        service.delete(5L);

        verify(adminRoleMapper).deleteById(10L);
        verify(roleMenuMapper).deleteById(20L);
        verify(roleMapper).deleteById(5L);
    }

    @Test
    void create_setsDefaults() {
        UmsRole role = new UmsRole();
        role.setName("r");
        service.create(role);
        assertEquals(1, role.getStatus());
        assertEquals(0, role.getDeleteFlag());
        assertNotNull(role.getCreateTime());
        verify(roleMapper).insert(role);
    }

    @Test
    void menus_returnsLinkedMenus() {
        UmsRoleMenu link = new UmsRoleMenu();
        link.setMenuId(2L);
        when(roleMenuMapper.selectList(any())).thenReturn(List.of(link));
        when(menuMapper.selectById(2L)).thenReturn(new com.macro.mall.model.UmsMenu());
        assertEquals(1, service.menus(1L).size());
    }
}
