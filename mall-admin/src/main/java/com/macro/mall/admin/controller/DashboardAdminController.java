package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.DashboardAdminServiceImpl;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.PmsProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "DashboardAdminController", description = "数据看板")
public class DashboardAdminController {

    private final DashboardAdminServiceImpl service;

    public DashboardAdminController(DashboardAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "经营概览统计")
    @GetMapping("/stats")
    public CommonResult<Map<String, Object>> stats() {
        return CommonResult.success(service.getStats());
    }

    @Operation(summary = "最近订单")
    @GetMapping("/recent-orders")
    public CommonResult<List<OmsOrder>> recentOrders() {
        return CommonResult.success(service.getRecentOrders());
    }

    @Operation(summary = "热销商品")
    @GetMapping("/hot-products")
    public CommonResult<List<PmsProduct>> hotProducts() {
        return CommonResult.success(service.getHotProducts());
    }

    @Operation(summary = "商品分类占比")
    @GetMapping("/category-stats")
    public CommonResult<List<Map<String, Object>>> categoryStats() {
        return CommonResult.success(service.getCategoryStats());
    }

    @Operation(summary = "近 7 天订单趋势")
    @GetMapping("/order-trend")
    public CommonResult<List<Map<String, Object>>> orderTrend() {
        return CommonResult.success(service.getOrderTrend());
    }
}
