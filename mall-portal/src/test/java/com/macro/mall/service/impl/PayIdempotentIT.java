package com.macro.mall.service.impl;

import com.macro.mall.mapper.CmsCartItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.CmsCartItem;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.OmsOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 支付幂等集成测试（@SpringBootTest + H2 内存库）。
 * 验证重复调用 payOrder 幂等：第二次不抛异常、订单状态与订单号不变。
 */
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test_portal;DB_CLOSE_DELAY=-1;MODE=MySQL"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PayIdempotentIT {

    @Autowired
    OmsOrderService orderService;
    @Autowired
    CmsCartItemMapper cartItemMapper;
    @Autowired
    OmsOrderMapper orderMapper;
    @Autowired
    PmsProductMapper productMapper;

    private static final Long MEMBER_ID = 555L;
    private static final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        cartItemMapper.logicalDeleteByMemberId(MEMBER_ID);
        CmsCartItem item = new CmsCartItem();
        item.setMemberId(MEMBER_ID);
        item.setProductId(PRODUCT_ID);
        item.setProductName("iPhone 15 Pro");
        item.setProductPrice(new BigDecimal("7999"));
        item.setQuantity(1);
        cartItemMapper.insert(item);
    }

    // 重复支付：第二次幂等、状态与订单号保持不变
    @Test
    void repeatedPayOrder_shouldBeIdempotent() {
        CmsCartItem cartItem = cartItemMapper.selectByMemberId(MEMBER_ID).get(0);
        Map<String, String> receiver = Map.of("name", "张三", "phone", "13800000000");

        OmsOrder order = orderService.createOrder(MEMBER_ID, List.of(cartItem.getId()), receiver);

        // 第一次支付
        orderService.payOrder(order.getId());
        OmsOrder first = orderMapper.selectByPrimaryKey(order.getId());
        assertEquals(1, first.getPayStatus());

        // 重复支付（支付网关重试）
        assertDoesNotThrow(() -> orderService.payOrder(order.getId()));
        OmsOrder second = orderMapper.selectByPrimaryKey(order.getId());
        assertEquals(1, second.getPayStatus());
        assertEquals(first.getOrderSn(), second.getOrderSn());
    }
}
