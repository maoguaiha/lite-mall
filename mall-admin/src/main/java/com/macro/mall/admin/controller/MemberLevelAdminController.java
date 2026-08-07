package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.MemberLevelAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/member/level")
@Tag(name = "MemberLevelAdminController", description = "会员等级管理")
public class MemberLevelAdminController {

    private final MemberLevelAdminServiceImpl service;

    public MemberLevelAdminController(MemberLevelAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询会员等级")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsMemberLevel>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增会员等级")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody UmsMemberLevel level) {
        service.create(level);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改会员等级")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody UmsMemberLevel level) {
        service.update(level);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除会员等级")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
