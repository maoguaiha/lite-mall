package com.macro.mall.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.controller.PayController;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 支付幂等单元测试（Mockito，纯逻辑，覆盖 PayController.payNotify）。
 * 锁定红线 ③/④：回调不得吞异常、重复回调幂等返回 success 且不重复支付。
 */
@ExtendWith(MockitoExtension.class)
class PayServiceTest {

    @Mock
    OmsOrderService orderService;

    @InjectMocks
    PayController payController;

    // 已支付订单的重复回调：直接返回 success，不再调用 payOrder
    @Test
    void payNotify_shouldReturnSuccess_whenAlreadyPaid() {
        OmsOrder paid = new OmsOrder();
        paid.setId(1L);
        paid.setPayStatus(1);
        when(orderService.getOrderById(1L)).thenReturn(paid);

        assertEquals("success", payController.payNotify(1L));
        verify(orderService, never()).payOrder(anyLong());
    }

    // 未支付订单回调：调用 payOrder 并统一返回 success
    @Test
    void payNotify_shouldPay_whenNotPaid() {
        OmsOrder unpaid = new OmsOrder();
        unpaid.setId(2L);
        unpaid.setPayStatus(0);
        when(orderService.getOrderById(2L)).thenReturn(unpaid);

        assertEquals("success", payController.payNotify(2L));
        verify(orderService).payOrder(2L);
    }

    // 订单不存在：异常上抛（不得吞异常，红线 ③），且不触发支付
    @Test
    void payNotify_shouldPropagate_whenOrderMissing() {
        when(orderService.getOrderById(99L)).thenThrow(new ApiException("订单不存在"));
        assertThrows(ApiException.class, () -> payController.payNotify(99L));
        verify(orderService, never()).payOrder(anyLong());
    }
}
