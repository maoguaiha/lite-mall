package com.macro.mall.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.CmsCartItemMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsProductSkuMapper;
import com.macro.mall.model.CmsCartItem;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductSku;
import com.macro.mall.service.CmsCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CmsCartServiceImpl implements CmsCartService {

    private final CmsCartItemMapper cartItemMapper;
    private final PmsProductMapper productMapper;
    private final PmsProductSkuMapper productSkuMapper;

    @Autowired
    public CmsCartServiceImpl(CmsCartItemMapper cartItemMapper, PmsProductMapper productMapper, PmsProductSkuMapper productSkuMapper) {
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
        this.productSkuMapper = productSkuMapper;
    }

    @Override
    public List<CmsCartItem> getCartList(Long memberId) {
        return cartItemMapper.selectByMemberId(memberId);
    }

    @Override
    public CmsCartItem addCart(Long memberId, Long productId, Long productSkuId, Integer quantity) {
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            Asserts.fail("商品不存在");
        }
        
        PmsProductSku sku = null;
        String skuAttributes = "";
        BigDecimal productPrice = product.getPrice();
        
        if (productSkuId != null) {
            sku = productSkuMapper.selectByPrimaryKey(productSkuId);
            if (sku == null) {
                Asserts.fail("SKU不存在");
            }
            skuAttributes = sku.getAttributes();
            productPrice = sku.getPrice();
        }
        
        CmsCartItem existItem = cartItemMapper.selectByMemberIdAndProductId(
                memberId, productId, productSkuId);
        
        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + quantity);
            existItem.setUpdateTime(new Date());
            cartItemMapper.updateByPrimaryKeySelective(existItem);
            return existItem;
        }
        
        CmsCartItem cartItem = new CmsCartItem();
        cartItem.setMemberId(memberId);
        cartItem.setProductId(productId);
        cartItem.setProductSkuId(productSkuId);
        cartItem.setProductName(product.getName());
        cartItem.setProductImage(product.getMainImage());
        cartItem.setProductPrice(productPrice);
        cartItem.setQuantity(quantity);
        cartItem.setSkuAttributes(skuAttributes);
        cartItem.setCreateTime(new Date());
        cartItem.setUpdateTime(new Date());
        cartItemMapper.insert(cartItem);
        return cartItem;
    }

    @Override
    public void updateCartItem(Long memberId, Long id, Integer quantity) {
        CmsCartItem cartItem = cartItemMapper.selectByMemberIdAndProductId(memberId, null, null);
        if (cartItem == null || !cartItem.getId().equals(id)) {
            Asserts.fail("购物车项不存在");
        }
        cartItem.setQuantity(quantity);
        cartItem.setUpdateTime(new Date());
        cartItemMapper.updateByPrimaryKeySelective(cartItem);
    }

    @Override
    public void deleteCartItem(Long memberId, Long id) {
        cartItemMapper.logicalDeleteByPrimaryKey(id);
    }

    @Override
    public void deleteCartItems(Long memberId, List<Long> ids) {
        cartItemMapper.logicalDeleteByMemberIdAndIds(memberId, ids);
    }

    @Override
    public void clearCart(Long memberId) {
        cartItemMapper.logicalDeleteByMemberId(memberId);
    }
}