package com.macro.mall.mapper;

import com.macro.mall.model.CmsCartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CmsCartItemMapper {
    List<CmsCartItem> selectByMemberId(@Param("memberId") Long memberId);
    
    CmsCartItem selectByMemberIdAndProductId(@Param("memberId") Long memberId, 
                                              @Param("productId") Long productId,
                                              @Param("productSkuId") Long productSkuId);
    
    int insert(CmsCartItem record);
    
    int updateByPrimaryKeySelective(CmsCartItem record);
    
    int logicalDeleteByPrimaryKey(@Param("id") Long id);
    
    int logicalDeleteByMemberId(@Param("memberId") Long memberId);
    
    int logicalDeleteByMemberIdAndIds(@Param("memberId") Long memberId, 
                                       @Param("ids") List<Long> ids);
}