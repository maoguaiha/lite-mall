package com.macro.mall.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.security.JwtTokenUtil;
import com.macro.mall.service.UmsMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * 会员管理Service实现类
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private final UmsMemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${wechat.appid:}")
    private String wechatAppid;

    @Value("${wechat.secret:}")
    private String wechatSecret;

    @Autowired
    public UmsMemberServiceImpl(UmsMemberMapper memberMapper, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void register(String username, String password, String phone, String nickname) {
        // 检查用户名是否已存在
        UmsMember existMember = memberMapper.selectByUsername(username);
        if (existMember != null) {
            Asserts.fail("该用户名已被注册");
        }
        // 检查手机号是否已存在
        UmsMember existPhone = memberMapper.selectByPhone(phone);
        if (existPhone != null) {
            Asserts.fail("该手机号已被注册");
        }
        // 创建会员
        UmsMember member = new UmsMember();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setPhone(phone);
        member.setNickname(nickname != null ? nickname : username);
        member.setStatus(1);
        member.setCreateTime(new Date());
        member.setUpdateTime(new Date());
        member.setMemberLevelId(1L); // 默认会员等级
        memberMapper.insert(member);
    }

    @Override
    public String login(String username, String password) {
        UmsMember member = memberMapper.selectByUsername(username);
        if (member == null) {
            Asserts.fail("该用户不存在");
        }
        if (member.getStatus() == 0) {
            Asserts.fail("该用户已被禁用");
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            Asserts.fail("密码错误");
        }
        return jwtTokenUtil.generateToken(username);
    }

    @Override
    public UmsMember getCurrentMember() {
        UsernamePasswordAuthenticationToken authentication = 
            (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Asserts.fail("用户未登录");
        }
        String username = (String) authentication.getPrincipal();
        return memberMapper.selectByUsername(username);
    }

    @Override
    public UmsMember getByUsername(String username) {
        return memberMapper.selectByUsername(username);
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        UmsMember currentMember = getCurrentMember();
        if (!passwordEncoder.matches(oldPassword, currentMember.getPassword())) {
            Asserts.fail("原密码错误");
        }
        UmsMember updateMember = new UmsMember();
        updateMember.setId(currentMember.getId());
        updateMember.setPassword(passwordEncoder.encode(newPassword));
        updateMember.setUpdateTime(new Date());
        memberMapper.updateByPrimaryKeySelective(updateMember);
    }

    @Override
    public void updateMember(UmsMember member) {
        UmsMember currentMember = getCurrentMember();
        member.setId(currentMember.getId());
        member.setUpdateTime(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public String loginByWeixin(String code) {
        // 1. 用 code 向微信换取 openid / session_key
        WeixinSession session = fetchWeixinSession(code);
        if (session == null) {
            Asserts.fail("微信登录失败，请重试");
        }
        if (session.getErrcode() != null && session.getErrcode() != 0) {
            Asserts.fail("微信登录失败：" + session.getErrmsg());
        }
        if (session.getOpenid() == null || session.getOpenid().isEmpty()) {
            Asserts.fail("微信登录失败，请重试");
        }
        // 2. 以 "wx_" + openid 作为唯一用户名，自动注册 / 登录
        String username = "wx_" + session.getOpenid();
        UmsMember member = memberMapper.selectByUsername(username);
        if (member == null) {
            member = new UmsMember();
            member.setUsername(username);
            // 微信用户不通过密码登录，用 openid 加密占位以满足 NOT NULL 约束
            member.setPassword(passwordEncoder.encode(session.getOpenid()));
            member.setNickname("微信用户");
            member.setStatus(1);
            member.setMemberLevelId(1L);
            member.setSourceType(1); // 1=小程序
            member.setCreateTime(new Date());
            member.setUpdateTime(new Date());
            memberMapper.insert(member);
            member = memberMapper.selectByUsername(username);
        }
        if (member.getStatus() == 0) {
            Asserts.fail("该用户已被禁用");
        }
        return jwtTokenUtil.generateToken(username);
    }

    /**
     * 调用微信 jscode2session 接口，用登录 code 换取 openid
     */
    private WeixinSession fetchWeixinSession(String code) {
        if (wechatAppid.isEmpty() || wechatSecret.isEmpty()) {
            return null;
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + wechatAppid
                + "&secret=" + wechatSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String resp = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(resp, WeixinSession.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 微信 jscode2session 接口返回结构
     */
    private static class WeixinSession {
        private String openid;
        private String session_key;
        private String unionid;
        private Integer errcode;
        private String errmsg;

        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        public String getSession_key() { return session_key; }
        public void setSession_key(String session_key) { this.session_key = session_key; }
        public String getUnionid() { return unionid; }
        public void setUnionid(String unionid) { this.unionid = unionid; }
        public Integer getErrcode() { return errcode; }
        public void setErrcode(Integer errcode) { this.errcode = errcode; }
        public String getErrmsg() { return errmsg; }
        public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    }
}
