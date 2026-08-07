package com.macro.mall.admin.service.impl;

import com.macro.mall.admin.security.AdminJwtTokenUtil;
import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.mapper.UmsAdminRoleAdminMapper;
import com.macro.mall.mapper.UmsRoleAdminMapper;
import com.macro.mall.model.UmsAdmin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminLoginServiceImplTest {

    @Mock
    UmsAdminMapper adminMapper;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AdminJwtTokenUtil jwtTokenUtil;
    @Mock
    UmsAdminRoleAdminMapper adminRoleMapper;
    @Mock
    UmsRoleAdminMapper roleMapper;

    @InjectMocks
    AdminLoginServiceImpl service;

    @Test
    void login_ok_returnsToken() {
        UmsAdmin admin = new UmsAdmin();
        admin.setPassword("enc");
        when(adminMapper.selectOne(any())).thenReturn(admin);
        when(passwordEncoder.matches("admin123", "enc")).thenReturn(true);
        when(jwtTokenUtil.generateToken(any())).thenReturn("tok");

        assertEquals("tok", service.login("admin", "admin123"));
    }

    @Test
    void login_notFound_throws() {
        when(adminMapper.selectOne(any())).thenReturn(null);
        assertThrows(ApiException.class, () -> service.login("admin", "x"));
    }

    @Test
    void login_wrongPassword_throws() {
        UmsAdmin admin = new UmsAdmin();
        admin.setPassword("enc");
        when(adminMapper.selectOne(any())).thenReturn(admin);
        when(passwordEncoder.matches(anyString(), eq("enc"))).thenReturn(false);
        assertThrows(ApiException.class, () -> service.login("admin", "x"));
    }
}
