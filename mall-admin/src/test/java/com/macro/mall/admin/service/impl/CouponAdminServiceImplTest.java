package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.model.SmsCoupon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CouponAdminServiceImplTest {

    @Mock
    SmsCouponMapper couponMapper;
    @Mock
    SmsCouponHistoryMapper historyMapper;

    @InjectMocks
    CouponAdminServiceImpl service;

    @Test
    void create_insertsAndResetsReceived() {
        SmsCoupon c = new SmsCoupon();
        c.setPublishCount(10);
        service.create(c);
        verify(couponMapper).insert(any(SmsCoupon.class));
        assertEquals(0, c.getReceivedCount());
    }

    @Test
    void delete_isLogicDelete() {
        service.delete(1L);
        verify(couponMapper).update(any(SmsCoupon.class), any());
        verify(couponMapper, never()).deleteById(anyLong());
    }
}
