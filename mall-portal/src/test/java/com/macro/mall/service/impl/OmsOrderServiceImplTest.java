package com.macro.mall.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.CmsCartItemMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.CmsCartItem;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.CmsCartService;
import com.macro.mall.service.PmsProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试（Mockito，纯逻辑，不依赖数据库/Spring 容器）。
 * 锁定红线修复：④ 支付幂等、①/⑧b 下单扣库存、⑫a 清空购物车时逻辑删。
 */
@ExtendWith(MockitoExtension.class)
class OmsOrderServiceImplTest {

    @Mock
    OmsOrderMapper orderMapper;
    @Mock
    OmsOrderItemMapper orderItemMapper;
    @Mock
    CmsCartItemMapper cartItemMapper;
    @Mock
    CmsCartService cartService;
    @Mock
    PmsProductService productService;

    @InjectMocks
    OmsOrderServiceImpl orderService;

    // ④ 已支付订单的重复回调直接返回，不抛异常、不重复更新
    @Test
    void payOrder_shouldBeIdempotent_whenAlreadyPaid() {
        OmsOrder paid = new OmsOrder();
        paid.setId(1L);
        paid.setPayStatus(1);
        when(orderMapper.selectByPrimaryKey(1L)).thenReturn(paid);

        assertDoesNotThrow(() -> orderService.payOrder(1L));

        verify(orderMapper, never()).updateByPrimaryKeySelective(any());
    }

    // ④ 未支付订单回调正常置为已支付
    @Test
    void payOrder_shouldUpdate_whenNotPaid() {
        OmsOrder unpaid = new OmsOrder();
        unpaid.setId(2L);
        unpaid.setPayStatus(0);
        when(orderMapper.selectByPrimaryKey(2L)).thenReturn(unpaid);

        orderService.payOrder(2L);

        ArgumentCaptor<OmsOrder> cap = ArgumentCaptor.forClass(OmsOrder.class);
        verify(orderMapper).updateByPrimaryKeySelective(cap.capture());
        assertEquals(1, cap.getValue().getPayStatus());
    }

    // 订单不存在 -> 抛 ApiException
    @Test
    void payOrder_shouldFail_whenOrderNotFound() {
        when(orderMapper.selectByPrimaryKey(99L)).thenReturn(null);
        assertThrows(ApiException.class, () -> orderService.payOrder(99L));
    }

    // ①/⑧b 下单时调用扣库存、写订单项、清空购物车
    @Test
    void createOrder_shouldDeductStockAndClearCart() {
        CmsCartItem item = new CmsCartItem();
        item.setId(10L);
        item.setProductId(1L);
        item.setQuantity(2);
        item.setProductPrice(new BigDecimal("100"));
        when(cartItemMapper.selectByMemberId(1L)).thenReturn(List.of(item));
        when(orderMapper.insert(any(OmsOrder.class))).thenAnswer(inv -> {
            ((OmsOrder) inv.getArgument(0)).setId(100L);
            return 1;
        });

        OmsOrder order = orderService.createOrder(1L, List.of(10L), receiverInfo());

        verify(productService).deductStock(1L, 2);
        verify(orderItemMapper).insert(any());
        verify(cartService).deleteCartItems(1L, List.of(10L));
        assertNotNull(order.getId());
    }

    // 购物车为空 -> 抛异常
    @Test
    void createOrder_shouldFail_whenCartEmpty() {
        when(cartItemMapper.selectByMemberId(1L)).thenReturn(Collections.emptyList());
        assertThrows(ApiException.class, () -> orderService.createOrder(1L, List.of(10L), receiverInfo()));
    }

    // 已支付订单不能取消
    @Test
    void cancelOrder_shouldFail_whenAlreadyPaid() {
        OmsOrder paid = new OmsOrder();
        paid.setId(5L);
        paid.setPayStatus(1);
        when(orderMapper.selectByPrimaryKey(5L)).thenReturn(paid);
        assertThrows(ApiException.class, () -> orderService.cancelOrder(5L));
    }

    private Map<String, String> receiverInfo() {
        Map<String, String> m = new java.util.HashMap<>();
        m.put("name", "张三");
        m.put("phone", "13800000000");
        return m;
    }
}
