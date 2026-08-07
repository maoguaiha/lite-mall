package com.macro.mall.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户模块单元测试（Mockito，纯逻辑，不依赖 DB/Spring 容器）。
 * 覆盖注册、登录、改密、当前用户查询与红线相关的异常分支。
 */
@ExtendWith(MockitoExtension.class)
class UmsMemberServiceTest {

    @Mock
    UmsMemberMapper memberMapper;
    @Mock
    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock
    JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    UmsMemberServiceImpl memberService;

    @BeforeEach
    void setUpAuth() {
        // 默认以登录用户 "u1" 注入 SecurityContext，updatePassword/getCurrentMember 依赖它
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("u1", null, List.of()));
    }

    // 注册：全新用户 -> 插入且密码加密、status=1
    @Test
    void register_shouldInsert_whenNewUser() {
        when(memberMapper.selectByUsername("u1")).thenReturn(null);
        when(memberMapper.selectByPhone("138")).thenReturn(null);
        when(passwordEncoder.encode("p1")).thenReturn("enc");

        memberService.register("u1", "p1", "138", "nick");

        ArgumentCaptor<UmsMember> cap = ArgumentCaptor.forClass(UmsMember.class);
        verify(memberMapper).insert(cap.capture());
        assertEquals("u1", cap.getValue().getUsername());
        assertEquals("enc", cap.getValue().getPassword());
        assertEquals(1, cap.getValue().getStatus());
    }

    // 注册：用户名已存在 -> 抛异常
    @Test
    void register_shouldFail_whenUsernameExists() {
        when(memberMapper.selectByUsername("u1")).thenReturn(new UmsMember());
        assertThrows(ApiException.class, () -> memberService.register("u1", "p1", "138", "nick"));
    }

    // 注册：手机号已存在 -> 抛异常
    @Test
    void register_shouldFail_whenPhoneExists() {
        when(memberMapper.selectByUsername("u1")).thenReturn(null);
        when(memberMapper.selectByPhone("138")).thenReturn(new UmsMember());
        assertThrows(ApiException.class, () -> memberService.register("u1", "p1", "138", "nick"));
    }

    // 登录：凭证正确 -> 返回 token
    @Test
    void login_shouldReturnToken_whenCredentialsOk() {
        UmsMember m = new UmsMember();
        m.setStatus(1);
        m.setPassword("enc");
        when(memberMapper.selectByUsername("u1")).thenReturn(m);
        when(passwordEncoder.matches("p1", "enc")).thenReturn(true);
        when(jwtTokenUtil.generateToken("u1")).thenReturn("tok");

        assertEquals("tok", memberService.login("u1", "p1"));
    }

    // 登录：用户不存在 -> 抛异常
    @Test
    void login_shouldFail_whenUserNotFound() {
        when(memberMapper.selectByUsername("u1")).thenReturn(null);
        assertThrows(ApiException.class, () -> memberService.login("u1", "p1"));
    }

    // 登录：用户被禁用 -> 抛异常
    @Test
    void login_shouldFail_whenDisabled() {
        UmsMember m = new UmsMember();
        m.setStatus(0);
        when(memberMapper.selectByUsername("u1")).thenReturn(m);
        assertThrows(ApiException.class, () -> memberService.login("u1", "p1"));
    }

    // 登录：密码错误 -> 抛异常
    @Test
    void login_shouldFail_whenWrongPassword() {
        UmsMember m = new UmsMember();
        m.setStatus(1);
        m.setPassword("enc");
        when(memberMapper.selectByUsername("u1")).thenReturn(m);
        when(passwordEncoder.matches("p1", "enc")).thenReturn(false);
        assertThrows(ApiException.class, () -> memberService.login("u1", "p1"));
    }

    // 改密：原密码正确 -> 更新密码
    @Test
    void updatePassword_shouldUpdate_whenOldCorrect() {
        UmsMember m = new UmsMember();
        m.setId(1L);
        m.setPassword("enc");
        when(memberMapper.selectByUsername("u1")).thenReturn(m);
        when(passwordEncoder.matches("old", "enc")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("enc2");

        memberService.updatePassword("old", "new");

        ArgumentCaptor<UmsMember> cap = ArgumentCaptor.forClass(UmsMember.class);
        verify(memberMapper).updateByPrimaryKeySelective(cap.capture());
        assertEquals(1L, cap.getValue().getId());
        assertEquals("enc2", cap.getValue().getPassword());
    }

    // 改密：原密码错误 -> 抛异常
    @Test
    void updatePassword_shouldFail_whenOldWrong() {
        UmsMember m = new UmsMember();
        m.setId(1L);
        m.setPassword("enc");
        when(memberMapper.selectByUsername("u1")).thenReturn(m);
        when(passwordEncoder.matches("wrong", "enc")).thenReturn(false);
        assertThrows(ApiException.class, () -> memberService.updatePassword("wrong", "new"));
    }

    // 当前用户：未登录 -> 抛异常（红线：未鉴权不得放行）
    @Test
    void getCurrentMember_shouldFail_whenNotLoggedIn() {
        SecurityContextHolder.clearContext();
        assertThrows(ApiException.class, () -> memberService.getCurrentMember());
    }
}
