package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.SmsSeckillProductMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.SmsSeckillProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 秒杀下单（独立事务 Bean，确保 @Transactional 通过代理生效，且不包裹 Redis 调用）
 */
@Service
public class SeckillOrderServiceImpl {

    private final SmsSeckillProductMapper seckillProductMapper;
    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final PmsProductMapper productMapper;

    public SeckillOrderServiceImpl(SmsSeckillProductMapper seckillProductMapper, OmsOrderMapper orderMapper,
                                   OmsOrderItemMapper orderItemMapper, PmsProductMapper productMapper) {
        this.seckillProductMapper = seckillProductMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createSeckillOrder(Long memberId, Long seckillProductId) {
        SmsSeckillProduct sp = seckillProductMapper.selectById(seckillProductId);
        if (sp == null) {
            Asserts.fail("秒杀商品不存在");
        }
        PmsProduct product = productMapper.selectByPrimaryKey(sp.getProductId());
        if (product == null) {
            Asserts.fail("商品不存在");
        }
        OmsOrder order = new OmsOrder();
        order.setOrderSn("SK" + System.currentTimeMillis() + memberId);
        order.setMemberId(memberId);
        order.setTotalAmount(sp.getSeckillPrice());
        order.setPayAmount(sp.getSeckillPrice());
        order.setStatus(0);
        orderMapper.insert(order);

        OmsOrderItem item = new OmsOrderItem();
        item.setOrderId(order.getId());
        item.setOrderSn(order.getOrderSn());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductPrice(sp.getSeckillPrice());
        item.setQuantity(1);
        item.setTotalPrice(sp.getSeckillPrice());
        orderItemMapper.insert(item);

        sp.setSeckillSales(sp.getSeckillSales() + 1);
        seckillProductMapper.updateById(sp);
        return order.getId();
    }
}
