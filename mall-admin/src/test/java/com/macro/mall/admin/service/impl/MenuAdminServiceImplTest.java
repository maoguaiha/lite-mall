package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.UmsMenuAdminMapper;
import com.macro.mall.mapper.UmsRoleMenuAdminMapper;
import com.macro.mall.model.UmsMenu;
import com.macro.mall.model.UmsRoleMenu;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuAdminServiceImplTest {

    @Mock
    UmsMenuAdminMapper menuMapper;
    @Mock
    UmsRoleMenuAdminMapper roleMenuMapper;
    @InjectMocks
    MenuAdminServiceImpl service;

    @Test
    void delete_cascadesAssociations() {
        UmsRoleMenu rm = new UmsRoleMenu();
        rm.setId(30L);
        when(roleMenuMapper.selectList(any())).thenReturn(List.of(rm));

        service.delete(7L);

        verify(roleMenuMapper).deleteById(30L);
        verify(menuMapper).deleteById(7L);
    }

    @Test
    void create_defaultsParentId() {
        UmsMenu menu = new UmsMenu();
        menu.setTitle("m");
        service.create(menu);
        assertEquals(0L, menu.getParentId());
        assertNotNull(menu.getCreateTime());
        verify(menuMapper).insert(menu);
    }

    @Test
    void listAll_returnsData() {
        when(menuMapper.selectList(any())).thenReturn(Collections.emptyList());
        assertNotNull(service.listAll());
    }
}
