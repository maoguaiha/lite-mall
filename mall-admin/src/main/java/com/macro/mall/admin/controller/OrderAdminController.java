package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.OrderAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Tag(name = "OrderAdminController", description = "订单管理")
public class OrderAdminController {

    private final OrderAdminServiceImpl service;

    public OrderAdminController(OrderAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "订单分页列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<OmsOrder>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(required = false) String memberUsername) {
        return CommonResult.success(service.list(pageNum, pageSize, status, memberUsername));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public CommonResult<OmsOrder> get(@PathVariable Long id) {
        return CommonResult.success(service.get(id));
    }

    @Operation(summary = "发货")
    @PostMapping("/ship")
    public CommonResult<Void> ship(@RequestParam Long id,
                                   @RequestParam String deliveryCompany,
                                   @RequestParam String deliverySn) {
        service.ship(id, deliveryCompany, deliverySn);
        return CommonResult.success(null);
    }

    @Operation(summary = "退款")
    @PostMapping("/refund")
    public CommonResult<Void> refund(@RequestParam Long id) {
        service.refund(id);
        return CommonResult.success(null);
    }
}
