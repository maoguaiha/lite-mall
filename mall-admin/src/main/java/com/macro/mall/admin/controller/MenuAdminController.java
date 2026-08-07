package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.MenuAdminServiceImpl;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMenu;
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
@RequestMapping("/admin/menu")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "MenuAdminController", description = "后台菜单管理")
public class MenuAdminController {

    private final MenuAdminServiceImpl service;

    public MenuAdminController(MenuAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "查询全部菜单")
    @GetMapping("/list")
    public CommonResult<List<UmsMenu>> listAll() {
        return CommonResult.success(service.listAll());
    }

    @Operation(summary = "新增菜单")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody UmsMenu menu) {
        service.create(menu);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改菜单")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody UmsMenu menu) {
        service.update(menu);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除菜单（级联清理关联）")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
