package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.ProductAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
@Tag(name = "ProductAdminController", description = "商品管理")
public class ProductAdminController {

    private final ProductAdminServiceImpl service;

    public ProductAdminController(ProductAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "商品分页列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<PmsProduct>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public CommonResult<PmsProduct> get(@PathVariable Long id) {
        return CommonResult.success(service.get(id));
    }

    @Operation(summary = "新增商品")
    @PostMapping("/create")
    public CommonResult<Void> create(@Valid @RequestBody PmsProduct product) {
        service.create(product);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改商品")
    @PostMapping("/update")
    public CommonResult<Void> update(@Valid @RequestBody PmsProduct product) {
        service.update(product);
        return CommonResult.success(null);
    }

    @Operation(summary = "上下架")
    @PostMapping("/publish")
    public CommonResult<Void> publish(@RequestParam Long id, @RequestParam Integer publishStatus) {
        service.updatePublishStatus(id, publishStatus);
        return CommonResult.success(null);
    }

    @Operation(summary = "调整库存")
    @PostMapping("/stock")
    public CommonResult<Void> stock(@RequestParam Long id, @RequestParam Integer stock) {
        service.updateStock(id, stock);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除商品（逻辑删）")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
