package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.service.UmsMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CouponServiceImpl {

    private final SmsCouponMapper couponMapper;
    private final SmsCouponHistoryMapper historyMapper;
    private final OmsOrderMapper orderMapper;
    private final UmsMemberService memberService;

    public CouponServiceImpl(SmsCouponMapper couponMapper, SmsCouponHistoryMapper historyMapper,
                             OmsOrderMapper orderMapper, UmsMemberService memberService) {
        this.couponMapper = couponMapper;
        this.historyMapper = historyMapper;
        this.orderMapper = orderMapper;
        this.memberService = memberService;
    }

    private Long currentMemberId() {
        return memberService.getCurrentMember().getId();
    }

    public List<SmsCoupon> center() {
        Date now = new Date();
        List<SmsCoupon> all = couponMapper.selectList(new QueryWrapper<SmsCoupon>()
                .eq("delete_flag", 0)
                .le("start_time", now)
                .ge("end_time", now)
                .orderByDesc("id"));
        List<SmsCoupon> available = new ArrayList<>();
        for (SmsCoupon c : all) {
            if (c.getReceivedCount() < c.getPublishCount()) {
                available.add(c);
            }
        }
        return available;
    }

    public List<SmsCouponHistory> myCoupons() {
        return historyMapper.selectList(new QueryWrapper<SmsCouponHistory>()
                .eq("member_id", currentMemberId())
                .eq("use_status", 1)
                .orderByDesc("id"));
    }

    @Transactional(rollbackFor = Exception.class)
    public void receive(Long couponId) {
        Long memberId = currentMemberId();
        SmsCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getDeleteFlag() != 0) {
            Asserts.fail("优惠券不存在");
        }
        long existed = historyMapper.selectCount(new QueryWrapper<SmsCouponHistory>()
                .eq("coupon_id", couponId).eq("member_id", memberId).in("use_status", 1, 2));
        if (existed > 0) {
            Asserts.fail("已领取该券");
        }
        if (coupon.getReceivedCount() >= coupon.getPublishCount()) {
            Asserts.fail("优惠券已抢光");
        }
        SmsCouponHistory history = new SmsCouponHistory();
        history.setCouponId(couponId);
        history.setMemberId(memberId);
        history.setUseStatus(1);
        history.setReceiveTime(new Date());
        historyMapper.insert(history);

        coupon.setReceivedCount(coupon.getReceivedCount() + 1);
        couponMapper.updateById(coupon);
    }

    @Transactional(rollbackFor = Exception.class)
    public BigDecimal useCoupon(Long couponId, Long orderId) {
        Long memberId = currentMemberId();
        SmsCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getDeleteFlag() != 0) {
            Asserts.fail("优惠券不存在");
        }
        Date now = new Date();
        if (now.before(coupon.getStartTime()) || now.after(coupon.getEndTime())) {
            Asserts.fail("优惠券不在有效期");
        }
        SmsCouponHistory history = historyMapper.selectOne(new QueryWrapper<SmsCouponHistory>()
                .eq("coupon_id", couponId).eq("member_id", memberId).eq("use_status", 1));
        if (history == null) {
            Asserts.fail("未领取该券");
        }
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null || !order.getMemberId().equals(memberId)) {
            Asserts.fail("订单不存在");
        }
        if (order.getPayAmount().compareTo(coupon.getMinPoint()) < 0) {
            Asserts.fail("未满足使用门槛");
        }
        history.setUseStatus(2);
        history.setOrderId(orderId);
        history.setUseTime(new Date());
        historyMapper.updateById(history);
        return coupon.getAmount();
    }
}
