package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.OrderSettingAdminServiceImpl;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrderSetting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order/setting")
@Tag(name = "OrderSettingAdminController", description = "订单设置")
public class OrderSettingAdminController {

    private final OrderSettingAdminServiceImpl service;

    public OrderSettingAdminController(OrderSettingAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "获取订单设置（单例）")
    @GetMapping("/get")
    public CommonResult<OmsOrderSetting> get() {
        return CommonResult.success(service.get());
    }

    @Operation(summary = "更新订单设置")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody OmsOrderSetting setting) {
        service.update(setting);
        return CommonResult.success(null);
    }
}
