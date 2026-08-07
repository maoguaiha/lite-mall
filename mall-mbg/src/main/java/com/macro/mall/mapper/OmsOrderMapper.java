package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OmsOrderMapper {
    int insert(OmsOrder record);
    
    OmsOrder selectByPrimaryKey(@Param("id") Long id);
    
    OmsOrder selectByOrderSn(@Param("orderSn") String orderSn);
    
    List<OmsOrder> selectByMemberId(@Param("memberId") Long memberId);
    
    List<OmsOrder> selectByMemberIdAndStatus(@Param("memberId") Long memberId, 
                                               @Param("status") Integer status);
    
    int updateByPrimaryKeySelective(OmsOrder record);
    
    int updatePayStatus(@Param("id") Long id, @Param("payStatus") Integer payStatus);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}