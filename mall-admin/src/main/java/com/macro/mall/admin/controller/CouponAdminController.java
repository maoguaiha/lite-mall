package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.CouponAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/coupon")
@Tag(name = "CouponAdminController", description = "优惠券管理")
public class CouponAdminController {

    private final CouponAdminServiceImpl service;

    public CouponAdminController(CouponAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "优惠券分页列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<SmsCoupon>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "优惠券详情")
    @GetMapping("/{id}")
    public CommonResult<SmsCoupon> get(@PathVariable Long id) {
        return CommonResult.success(service.get(id));
    }

    @Operation(summary = "新增优惠券")
    @PostMapping("/create")
    public CommonResult<SmsCoupon> create(@Valid @RequestBody SmsCoupon coupon) {
        return CommonResult.success(service.create(coupon));
    }

    @Operation(summary = "修改优惠券")
    @PostMapping("/update")
    public CommonResult<Void> update(@Valid @RequestBody SmsCoupon coupon) {
        service.update(coupon);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除优惠券（逻辑删）")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "领取/核销记录")
    @GetMapping("/histories")
    public CommonResult<CommonPage<SmsCouponHistory>> histories(@RequestParam Long couponId,
                                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(service.histories(couponId, pageNum, pageSize));
    }
}
