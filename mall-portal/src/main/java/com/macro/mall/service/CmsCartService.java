package com.macro.mall.service;

import com.macro.mall.model.CmsCartItem;

import java.util.List;

public interface CmsCartService {
    List<CmsCartItem> getCartList(Long memberId);
    
    CmsCartItem addCart(Long memberId, Long productId, Long productSkuId, Integer quantity);
    
    void updateCartItem(Long memberId, Long id, Integer quantity);
    
    void deleteCartItem(Long memberId, Long id);
    
    void deleteCartItems(Long memberId, List<Long> ids);
    
    void clearCart(Long memberId);
}