package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsMemberLoginParam;
import com.macro.mall.dto.UmsMemberParam;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 会员管理Controller
 */
@Tag(name = "UmsMemberController", description = "会员管理")
@RestController
@RequestMapping("/member")
public class UmsMemberController {
    private final UmsMemberService memberService;

    @Autowired
    public UmsMemberController(UmsMemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public CommonResult register(@Valid @RequestBody UmsMemberParam umsMemberParam) {
        memberService.register(umsMemberParam.getUsername(), umsMemberParam.getPassword(),
                umsMemberParam.getPhone(), umsMemberParam.getNickname());
        return CommonResult.success(null, "注册成功");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public CommonResult login(@Valid @RequestBody UmsMemberLoginParam umsMemberLoginParam) {
        String token = memberService.login(umsMemberLoginParam.getUsername(), umsMemberLoginParam.getPassword());
        return CommonResult.success(token, "登录成功");
    }

    @Operation(summary = "微信小程序登录")
    @PostMapping("/loginByWeixin")
    public CommonResult loginByWeixin(@RequestParam String code) {
        String token = memberService.loginByWeixin(code);
        return CommonResult.success(token, "登录成功");
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public CommonResult<UmsMember> getMemberInfo() {
        UmsMember member = memberService.getCurrentMember();
        member.setPassword(null); // 不返回密码
        return CommonResult.success(member);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    public CommonResult updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        memberService.updatePassword(oldPassword, newPassword);
        return CommonResult.success(null, "密码修改成功");
    }

    @Operation(summary = "更新会员信息")
    @PostMapping("/update")
    public CommonResult updateMember(@RequestBody UmsMember member) {
        memberService.updateMember(member);
        return CommonResult.success(null, "更新成功");
    }
}
