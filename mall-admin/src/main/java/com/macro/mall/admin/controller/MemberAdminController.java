package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.MemberAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/member")
@Tag(name = "MemberAdminController", description = "会员用户管理")
public class MemberAdminController {

    private final MemberAdminServiceImpl service;

    public MemberAdminController(MemberAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "会员分页列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsMember>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "会员详情")
    @GetMapping("/{id}")
    public CommonResult<UmsMember> get(@PathVariable Long id) {
        return CommonResult.success(service.get(id));
    }

    @Operation(summary = "启用/禁用会员")
    @PostMapping("/status")
    public CommonResult<Void> status(@RequestParam Long id, @RequestParam Integer status) {
        service.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @Operation(summary = "禁用会员（逻辑删）")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
