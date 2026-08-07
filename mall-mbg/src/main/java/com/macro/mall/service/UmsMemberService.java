package com.macro.mall.service;

import com.macro.mall.model.UmsMember;

public interface UmsMemberService {
    void register(String username, String password, String phone, String nickname);
    String login(String username, String password);
    /**
     * 微信小程序登录：用 wx.login 获取的 code 换取 openid，
     * 自动注册（username = "wx_" + openid）并返回 JWT token
     */
    String loginByWeixin(String code);
    UmsMember getCurrentMember();
    UmsMember getByUsername(String username);
    void updatePassword(String oldPassword, String newPassword);
    void updateMember(UmsMember member);
}