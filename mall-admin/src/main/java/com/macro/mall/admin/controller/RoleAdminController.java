package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.RoleAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMenu;
import com.macro.mall.model.UmsRole;
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
@RequestMapping("/admin/role")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "RoleAdminController", description = "后台角色管理")
public class RoleAdminController {

    private final RoleAdminServiceImpl service;

    public RoleAdminController(RoleAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsRole>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody UmsRole role) {
        service.create(role);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改角色")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody UmsRole role) {
        service.update(role);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除角色（级联清理关联）")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "查询角色拥有的菜单")
    @GetMapping("/menus")
    public CommonResult<List<UmsMenu>> menus(@RequestParam Long roleId) {
        return CommonResult.success(service.menus(roleId));
    }
}
