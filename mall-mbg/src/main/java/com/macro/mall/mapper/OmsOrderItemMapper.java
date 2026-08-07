package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OmsOrderItemMapper {
    int insert(OmsOrderItem record);
    
    int insertList(@Param("list") List<OmsOrderItem> list);
    
    List<OmsOrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    List<OmsOrderItem> selectByOrderSn(@Param("orderSn") String orderSn);
}