package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponAdminServiceImpl {

    private final SmsCouponMapper couponMapper;
    private final SmsCouponHistoryMapper historyMapper;

    public CouponAdminServiceImpl(SmsCouponMapper couponMapper, SmsCouponHistoryMapper historyMapper) {
        this.couponMapper = couponMapper;
        this.historyMapper = historyMapper;
    }

    public CommonPage<SmsCoupon> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return CommonPage.restPage(couponMapper.selectList(new QueryWrapper<SmsCoupon>().orderByDesc("id")));
    }

    public SmsCoupon get(Long id) {
        SmsCoupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            Asserts.fail("优惠券不存在");
        }
        return coupon;
    }

    public SmsCoupon create(SmsCoupon coupon) {
        coupon.setReceivedCount(0);
        couponMapper.insert(coupon);
        return coupon;
    }

    public void update(SmsCoupon coupon) {
        if (coupon.getId() == null) {
            Asserts.fail("优惠券ID不能为空");
        }
        couponMapper.updateById(coupon);
    }

    public void delete(Long id) {
        couponMapper.update(new SmsCoupon(),
                new UpdateWrapper<SmsCoupon>().eq("id", id).set("delete_flag", 2));
    }

    public CommonPage<SmsCouponHistory> histories(Long couponId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return CommonPage.restPage(historyMapper.selectList(
                new QueryWrapper<SmsCouponHistory>().eq("coupon_id", couponId).orderByDesc("id")));
    }
}
