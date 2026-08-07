package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.AdminUserServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "AdminUserController", description = "商家端管理员管理")
public class AdminUserController {

    private final AdminUserServiceImpl service;

    public AdminUserController(AdminUserServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询管理员")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsAdmin>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增管理员")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody UmsAdmin admin) {
        service.create(admin);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改管理员")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody UmsAdmin admin) {
        service.update(admin);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除管理员")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "管理员列表（不分页，用于角色分配等）")
    @GetMapping("/all")
    public CommonResult<List<UmsAdmin>> all() {
        return CommonResult.success(service.all());
    }
}
