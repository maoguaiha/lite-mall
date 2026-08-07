package com.macro.mall.service.impl;

import com.macro.mall.controller.PayController;
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
 * 支付验收测试（@SpringBootTest + H2 内存库）。
 * 全链路：建购物车项 -> 下单(扣库存+逻辑删购物车) -> 支付回调(幂等) -> 状态/库存校验。
 */
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test_portal;DB_CLOSE_DELAY=-1;MODE=MySQL"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PayAcceptanceTest {

    @Autowired
    OmsOrderService orderService;
    @Autowired
    PayController payController;
    @Autowired
    CmsCartItemMapper cartItemMapper;
    @Autowired
    OmsOrderMapper orderMapper;
    @Autowired
    PmsProductMapper productMapper;

    private static final Long MEMBER_ID = 666L;
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

    // 下单 -> 支付网关回调(首次+重复) -> 订单置已支付、库存仅扣一次
    @Test
    void fullPayFlow_shouldBeIdempotent() {
        PmsProduct before = productMapper.selectByPrimaryKey(PRODUCT_ID);
        int oldStock = before.getStock();

        CmsCartItem cartItem = cartItemMapper.selectByMemberId(MEMBER_ID).get(0);
        Map<String, String> receiver = Map.of("name", "张三", "phone", "13800000000");

        OmsOrder order = orderService.createOrder(MEMBER_ID, List.of(cartItem.getId()), receiver);
        assertNotNull(order.getId());
        assertEquals(0, order.getPayStatus());

        // 首次回调
        assertEquals("success", payController.payNotify(order.getId()));
        OmsOrder paid = orderMapper.selectByPrimaryKey(order.getId());
        assertEquals(1, paid.getPayStatus());

        // 重复回调：仍是 success、状态不变
        assertEquals("success", payController.payNotify(order.getId()));
        OmsOrder paidAgain = orderMapper.selectByPrimaryKey(order.getId());
        assertEquals(1, paidAgain.getPayStatus());

        // 库存只扣一次（扣减发生在下单，支付不再扣）
        PmsProduct after = productMapper.selectByPrimaryKey(PRODUCT_ID);
        assertEquals(oldStock - 1, after.getStock());

        // 购物车项已逻辑删除
        assertTrue(cartItemMapper.selectByMemberId(MEMBER_ID).isEmpty());
    }
}
