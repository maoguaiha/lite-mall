package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.service.UmsMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CouponServiceImplTest {

    @Mock
    SmsCouponMapper couponMapper;
    @Mock
    SmsCouponHistoryMapper historyMapper;
    @Mock
    OmsOrderMapper orderMapper;
    @Mock
    UmsMemberService memberService;

    @InjectMocks
    CouponServiceImpl service;

    private void withMember() {
        com.macro.mall.model.UmsMember m = new com.macro.mall.model.UmsMember();
        m.setId(1L);
        when(memberService.getCurrentMember()).thenReturn(m);
    }

    private SmsCoupon baseCoupon() {
        SmsCoupon c = new SmsCoupon();
        c.setId(1L);
        c.setAmount(new BigDecimal("20"));
        c.setMinPoint(new BigDecimal("100"));
        c.setPublishCount(10);
        c.setReceivedCount(0);
        c.setDeleteFlag(0);
        c.setStartTime(new Date(System.currentTimeMillis() - 1000));
        c.setEndTime(new Date(System.currentTimeMillis() + 86400000));
        return c;
    }

    @Test
    void receive_success_incrementsReceived() {
        withMember();
        when(couponMapper.selectById(1L)).thenReturn(baseCoupon());
        when(historyMapper.selectCount(any())).thenReturn(0L);
        service.receive(1L);
        verify(historyMapper).insert(any(SmsCouponHistory.class));
        verify(couponMapper).updateById(any(SmsCoupon.class));
    }

    @Test
    void receive_alreadyReceived_throws() {
        withMember();
        when(couponMapper.selectById(1L)).thenReturn(baseCoupon());
        when(historyMapper.selectCount(any())).thenReturn(1L);
        assertThrows(ApiException.class, () -> service.receive(1L));
    }

    @Test
    void use_success_returnsDiscount() {
        withMember();
        when(couponMapper.selectById(1L)).thenReturn(baseCoupon());
        SmsCouponHistory h = new SmsCouponHistory();
        h.setUseStatus(1);
        when(historyMapper.selectOne(any())).thenReturn(h);
        OmsOrder order = new OmsOrder();
        order.setMemberId(1L);
        order.setPayAmount(new BigDecimal("200"));
        when(orderMapper.selectByPrimaryKey(2L)).thenReturn(order);
        assertEquals(new BigDecimal("20"), service.useCoupon(1L, 2L));
    }

    @Test
    void use_thresholdNotMet_throws() {
        withMember();
        when(couponMapper.selectById(1L)).thenReturn(baseCoupon());
        SmsCouponHistory h = new SmsCouponHistory();
        h.setUseStatus(1);
        when(historyMapper.selectOne(any())).thenReturn(h);
        OmsOrder order = new OmsOrder();
        order.setMemberId(1L);
        order.setPayAmount(new BigDecimal("50"));
        when(orderMapper.selectByPrimaryKey(2L)).thenReturn(order);
        assertThrows(ApiException.class, () -> service.useCoupon(1L, 2L));
    }
}
