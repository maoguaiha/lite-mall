package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.portal.service.impl.CouponServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/coupon")
@Tag(name = "CouponController", description = "优惠券（领券/我的券/抵扣）")
public class CouponController {

    private final CouponServiceImpl couponService;

    public CouponController(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "领券中心")
    @GetMapping("/center")
    public CommonResult<List<SmsCoupon>> center() {
        return CommonResult.success(couponService.center());
    }

    @Operation(summary = "我的优惠券")
    @GetMapping("/my")
    public CommonResult<List<SmsCouponHistory>> my() {
        return CommonResult.success(couponService.myCoupons());
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/receive")
    public CommonResult<Void> receive(@RequestParam Long couponId) {
        couponService.receive(couponId);
        return CommonResult.success(null);
    }

    @Operation(summary = "使用优惠券（返回抵扣金额）")
    @PostMapping("/use")
    public CommonResult<BigDecimal> use(@RequestParam Long couponId, @RequestParam Long orderId) {
        return CommonResult.success(couponService.useCoupon(couponId, orderId));
    }
}
