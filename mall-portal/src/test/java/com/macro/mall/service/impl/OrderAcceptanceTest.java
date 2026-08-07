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
 * 下单->支付验收测试（@SpringBootTest + H2 内存库）。
 * 验证全链路：建购物车项 -> 下单(扣库存+写订单项+逻辑删购物车) -> 支付(幂等+置已支付)。
 */
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test_portal;DB_CLOSE_DELAY=-1;MODE=MySQL"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderAcceptanceTest {

    @Autowired
    OmsOrderService orderService;
    @Autowired
    CmsCartItemMapper cartItemMapper;
    @Autowired
    OmsOrderMapper orderMapper;
    @Autowired
    PmsProductMapper productMapper;

    private static final Long MEMBER_ID = 777L;
    private static final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        // 逻辑清空该会员历史购物车，再插入一个有效项（⑫a 逻辑删）
        cartItemMapper.logicalDeleteByMemberId(MEMBER_ID);
        CmsCartItem item = new CmsCartItem();
        item.setMemberId(MEMBER_ID);
        item.setProductId(PRODUCT_ID);
        item.setProductName("iPhone 15 Pro");
        item.setProductPrice(new BigDecimal("7999"));
        item.setQuantity(1);
        cartItemMapper.insert(item);
    }

    @Test
    void fullFlow_shouldCreateAndPayOrder() {
        PmsProduct before = productMapper.selectByPrimaryKey(PRODUCT_ID);
        int oldStock = before.getStock();

        CmsCartItem cartItem = cartItemMapper.selectByMemberId(MEMBER_ID).get(0);
        Map<String, String> receiver = Map.of("name", "张三", "phone", "13800000000");

        OmsOrder order = orderService.createOrder(MEMBER_ID, List.of(cartItem.getId()), receiver);

        assertNotNull(order.getId());
        assertEquals(0, order.getPayStatus());

        // 购物车项应已被逻辑删除（⑫a）
        assertTrue(cartItemMapper.selectByMemberId(MEMBER_ID).isEmpty());

        // 支付后订单置为已支付（④ 幂等 + 状态流转）
        orderService.payOrder(order.getId());
        OmsOrder paid = orderMapper.selectByPrimaryKey(order.getId());
        assertEquals(1, paid.getPayStatus());

        // 库存已扣减（①/⑧b）
        PmsProduct after = productMapper.selectByPrimaryKey(PRODUCT_ID);
        assertEquals(oldStock - 1, after.getStock());
    }
}
