package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.CategoryAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product/category")
@Tag(name = "CategoryAdminController", description = "商品分类管理")
public class CategoryAdminController {

    private final CategoryAdminServiceImpl service;

    public CategoryAdminController(CategoryAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询商品分类")
    @GetMapping("/list")
    public CommonResult<CommonPage<PmsProductCategory>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "分类下拉选项")
    @GetMapping("/options")
    public CommonResult<List<PmsProductCategory>> options() {
        return CommonResult.success(service.options());
    }

    @Operation(summary = "新增商品分类")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody PmsProductCategory category) {
        service.create(category);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改商品分类")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody PmsProductCategory category) {
        service.update(category);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除商品分类")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
