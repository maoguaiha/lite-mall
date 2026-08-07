package com.macro.mall.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.CmsCartItemMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsProductSkuMapper;
import com.macro.mall.model.CmsCartItem;
import com.macro.mall.model.PmsProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 购物车模块单元测试（Mockito，纯逻辑）。
 * 覆盖加入/更新/删除，并锁定红线 ⑫a：删除一律走逻辑删（logicalDelete*）。
 */
@ExtendWith(MockitoExtension.class)
class CmsCartServiceTest {

    @Mock
    CmsCartItemMapper cartItemMapper;
    @Mock
    PmsProductMapper productMapper;
    @Mock
    PmsProductSkuMapper productSkuMapper;

    @InjectMocks
    CmsCartServiceImpl cartService;

    // 加入：商品不存在 -> 抛异常
    @Test
    void addCart_shouldFail_whenProductMissing() {
        when(productMapper.selectByPrimaryKey(1L)).thenReturn(null);
        assertThrows(ApiException.class, () -> cartService.addCart(1L, 1L, null, 2));
    }

    // 加入：新商品 -> 插入
    @Test
    void addCart_shouldInsert_whenNew() {
        PmsProduct p = new PmsProduct();
        p.setId(1L);
        p.setName("P");
        p.setPrice(new BigDecimal("10"));
        p.setMainImage("img");
        when(productMapper.selectByPrimaryKey(1L)).thenReturn(p);
        when(cartItemMapper.selectByMemberIdAndProductId(anyLong(), anyLong(), any())).thenReturn(null);
        when(cartItemMapper.insert(any(CmsCartItem.class))).thenReturn(1);

        CmsCartItem r = cartService.addCart(1L, 1L, null, 2);
        assertEquals(1L, r.getProductId());
        assertEquals(2, r.getQuantity());
        verify(cartItemMapper).insert(any(CmsCartItem.class));
    }

    // 加入：已存在 -> 累加数量并更新
    @Test
    void addCart_shouldUpdate_whenExists() {
        PmsProduct p = new PmsProduct();
        p.setId(1L);
        p.setName("P");
        p.setPrice(new BigDecimal("10"));
        p.setMainImage("img");
        when(productMapper.selectByPrimaryKey(1L)).thenReturn(p);
        CmsCartItem exist = new CmsCartItem();
        exist.setId(5L);
        exist.setQuantity(1);
        when(cartItemMapper.selectByMemberIdAndProductId(anyLong(), anyLong(), any())).thenReturn(exist);
        when(cartItemMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        CmsCartItem r = cartService.addCart(1L, 1L, null, 3);
        assertEquals(5L, r.getId());
        assertEquals(4, r.getQuantity()); // 1 + 3
        verify(cartItemMapper).updateByPrimaryKeySelective(any());
    }

    // 删除单项：走逻辑删（⑫a）
    @Test
    void deleteCartItem_shouldLogicalDelete() {
        cartService.deleteCartItem(1L, 9L);
        verify(cartItemMapper).logicalDeleteByPrimaryKey(9L);
    }

    // 清空购物车：走逻辑删（⑫a）
    @Test
    void clearCart_shouldLogicalDelete() {
        cartService.clearCart(1L);
        verify(cartItemMapper).logicalDeleteByMemberId(1L);
    }
}
