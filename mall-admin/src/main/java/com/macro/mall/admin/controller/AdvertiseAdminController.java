package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.AdvertiseAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsHomeAdvertise;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/advertise")
@Tag(name = "AdvertiseAdminController", description = "首页广告管理")
public class AdvertiseAdminController {

    private final AdvertiseAdminServiceImpl service;

    public AdvertiseAdminController(AdvertiseAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询广告")
    @GetMapping("/list")
    public CommonResult<CommonPage<SmsHomeAdvertise>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增广告")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody SmsHomeAdvertise advertise) {
        service.create(advertise);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改广告")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody SmsHomeAdvertise advertise) {
        service.update(advertise);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除广告")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
