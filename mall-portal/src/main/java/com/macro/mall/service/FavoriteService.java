package com.macro.mall.service;

import com.macro.mall.model.UmsMemberFavorite;

import java.util.List;

public interface FavoriteService {
    /**
     * 收藏商品（已收藏则忽略），快照商品名称/主图/价格
     */
    void add(Long memberId, Long productId);

    /**
     * 取消收藏
     */
    void remove(Long memberId, Long productId);

    /**
     * 当前会员的收藏列表（按收藏时间倒序）
     */
    List<UmsMemberFavorite> list(Long memberId);

    /**
     * 是否已收藏
     */
    boolean check(Long memberId, Long productId);
}
