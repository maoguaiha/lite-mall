package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.UmsMemberFavoriteMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.UmsMemberFavorite;
import com.macro.mall.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final UmsMemberFavoriteMapper favoriteMapper;
    private final PmsProductMapper productMapper;

    @Autowired
    public FavoriteServiceImpl(UmsMemberFavoriteMapper favoriteMapper, PmsProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void add(Long memberId, Long productId) {
        LambdaQueryWrapper<UmsMemberFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberFavorite::getMemberId, memberId)
                .eq(UmsMemberFavorite::getProductId, productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            return; // 已收藏，去重
        }
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        UmsMemberFavorite favorite = new UmsMemberFavorite();
        favorite.setMemberId(memberId);
        favorite.setProductId(productId);
        if (product != null) {
            favorite.setProductName(product.getName());
            favorite.setProductPic(product.getMainImage());
            favorite.setProductPrice(product.getPrice());
        }
        favorite.setCreateTime(new Date());
        favoriteMapper.insert(favorite);
    }

    @Override
    public void remove(Long memberId, Long productId) {
        LambdaQueryWrapper<UmsMemberFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberFavorite::getMemberId, memberId)
                .eq(UmsMemberFavorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
    }

    @Override
    public List<UmsMemberFavorite> list(Long memberId) {
        LambdaQueryWrapper<UmsMemberFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberFavorite::getMemberId, memberId)
                .orderByDesc(UmsMemberFavorite::getCreateTime);
        return favoriteMapper.selectList(wrapper);
    }

    @Override
    public boolean check(Long memberId, Long productId) {
        LambdaQueryWrapper<UmsMemberFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberFavorite::getMemberId, memberId)
                .eq(UmsMemberFavorite::getProductId, productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}
