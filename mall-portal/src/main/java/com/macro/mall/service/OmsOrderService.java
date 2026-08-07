package com.macro.mall.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;

import java.util.List;
import java.util.Map;

public interface OmsOrderService {
    OmsOrder createOrder(Long memberId, List<Long> cartItemIds, Map<String, String> receiverInfo);
    
    OmsOrder getOrderById(Long id);
    
    OmsOrder getOrderBySn(String orderSn);
    
    List<OmsOrder> getOrderList(Long memberId);
    
    List<OmsOrder> getOrderListByStatus(Long memberId, Integer status);
    
    CommonPage<OmsOrder> getOrderListPage(Long memberId, Integer status, 
                                           Integer pageNum, Integer pageSize);
    
    List<OmsOrderItem> getOrderItems(Long orderId);
    
    void payOrder(Long orderId);
    
    void cancelOrder(Long orderId);
    
    void confirmOrder(Long orderId);
}