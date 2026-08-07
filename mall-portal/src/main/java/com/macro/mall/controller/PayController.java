package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "PayController", description = "支付管理")
@RestController
@RequestMapping("/pay")
public class PayController {

    private final OmsOrderService orderService;

    @Autowired
    public PayController(OmsOrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "发起支付")
    @PostMapping("/create")
    public CommonResult<Map<String, Object>> createPay(@RequestParam Long orderId) {
        OmsOrder order = orderService.getOrderById(orderId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderSn", order.getOrderSn());
        result.put("amount", order.getPayAmount());
        result.put("payUrl", "/pay/notify/" + orderId);
        
        return CommonResult.success(result);
    }

    @Operation(summary = "模拟支付回调")
    @PostMapping("/notify/{orderId}")
    public String payNotify(@PathVariable Long orderId) {
        // 幂等：已支付订单直接返回 success，避免支付网关重复回调被误判为失败（红线 ③/④）
        OmsOrder order = orderService.getOrderById(orderId);
        if (order.getPayStatus() == 1) {
            return "success";
        }
        orderService.payOrder(orderId);
        return "success";
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/status/{orderId}")
    public CommonResult<Map<String, Object>> getPayStatus(@PathVariable Long orderId) {
        OmsOrder order = orderService.getOrderById(orderId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderSn", order.getOrderSn());
        result.put("payStatus", order.getPayStatus());
        result.put("status", order.getStatus());
        
        return CommonResult.success(result);
    }
}