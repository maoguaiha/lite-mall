package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.CmsCartItemMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.CmsCartItem;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.service.CmsCartService;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OmsOrderServiceImpl implements OmsOrderService {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final CmsCartItemMapper cartItemMapper;
    private final CmsCartService cartService;
    private final PmsProductService productService;

    @Autowired
    public OmsOrderServiceImpl(OmsOrderMapper orderMapper, OmsOrderItemMapper orderItemMapper, CmsCartItemMapper cartItemMapper, CmsCartService cartService, PmsProductService productService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartItemMapper = cartItemMapper;
        this.cartService = cartService;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsOrder createOrder(Long memberId, List<Long> cartItemIds, Map<String, String> receiverInfo) {
        List<CmsCartItem> cartItems = new ArrayList<>(cartItemMapper.selectByMemberId(memberId));
        cartItems.removeIf(item -> !cartItemIds.contains(item.getId()));
        
        if (cartItems.isEmpty()) {
            Asserts.fail("购物车为空");
        }
        
        String orderSn = generateOrderSn();
        
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        OmsOrder order = new OmsOrder();
        order.setOrderSn(orderSn);
        order.setMemberId(memberId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setPayStatus(0);
        order.setStatus(0);
        order.setReceiverName(receiverInfo.get("name"));
        order.setReceiverPhone(receiverInfo.get("phone"));
        order.setReceiverProvince(receiverInfo.get("province"));
        order.setReceiverCity(receiverInfo.get("city"));
        order.setReceiverDistrict(receiverInfo.get("district"));
        order.setReceiverDetailAddress(receiverInfo.get("detailAddress"));
        order.setCreateTime(new Date());
        orderMapper.insert(order);
        
        for (CmsCartItem cartItem : cartItems) {
            productService.deductStock(cartItem.getProductId(), cartItem.getQuantity());
            
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(orderSn);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductSkuId(cartItem.getProductSkuId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductImage(cartItem.getProductImage());
            orderItem.setProductPrice(cartItem.getProductPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItem.setCreateTime(new Date());
            orderItemMapper.insert(orderItem);
        }
        
        cartService.deleteCartItems(memberId, cartItemIds);
        
        return order;
    }

    private String generateOrderSn() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public OmsOrder getOrderById(Long id) {
        OmsOrder order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        return order;
    }

    @Override
    public OmsOrder getOrderBySn(String orderSn) {
        OmsOrder order = orderMapper.selectByOrderSn(orderSn);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        return order;
    }

    @Override
    public List<OmsOrder> getOrderList(Long memberId) {
        return orderMapper.selectByMemberId(memberId);
    }

    @Override
    public List<OmsOrder> getOrderListByStatus(Long memberId, Integer status) {
        return orderMapper.selectByMemberIdAndStatus(memberId, status);
    }

    @Override
    public CommonPage<OmsOrder> getOrderListPage(Long memberId, Integer status,
                                                 Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OmsOrder> orderList;
        if (status != null) {
            orderList = orderMapper.selectByMemberIdAndStatus(memberId, status);
        } else {
            orderList = orderMapper.selectByMemberId(memberId);
        }
        return CommonPage.restPage(orderList);
    }

    @Override
    public List<OmsOrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId) {
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        if (order.getPayStatus() == 1) {
            return; // 幂等：重复支付回调直接返回，不抛异常（红线 ④）
        }
        order.setPayStatus(1);
        order.setStatus(1);
        order.setPayTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        if (order.getPayStatus() == 1) {
            Asserts.fail("已支付订单不能取消");
        }
        order.setStatus(-1);
        order.setCancelTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        order.setStatus(3);
        order.setReceiveTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }
}