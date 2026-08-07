package com.macro.mall.admin.controller;

import com.macro.mall.admin.dto.AdminInfo;
import com.macro.mall.admin.dto.AdminLoginParam;
import com.macro.mall.admin.service.impl.AdminLoginServiceImpl;
import com.macro.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "AdminLoginController", description = "商家端登录")
public class AdminLoginController {

    private final AdminLoginServiceImpl loginService;

    public AdminLoginController(AdminLoginServiceImpl loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public CommonResult<String> login(@Valid @RequestBody AdminLoginParam param) {
        String token = loginService.login(param.getUsername(), param.getPassword());
        return CommonResult.success(token);
    }

    @Operation(summary = "获取当前登录管理员信息（含角色）")
    @GetMapping("/info")
    public CommonResult<AdminInfo> info(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return CommonResult.failed("未登录");
        }
        return CommonResult.success(loginService.getAdminInfo(auth.substring(7)));
    }
}
