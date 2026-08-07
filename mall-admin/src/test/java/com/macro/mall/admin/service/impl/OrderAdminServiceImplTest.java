package com.macro.mall.admin.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.OmsOrderAdminMapper;
import com.macro.mall.model.OmsOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderAdminServiceImplTest {

    @Mock
    OmsOrderAdminMapper orderAdminMapper;

    @InjectMocks
    OrderAdminServiceImpl service;

    @Test
    void ship_setsStatus2() {
        when(orderAdminMapper.update(any(), any())).thenReturn(1);
        service.ship(1L, "SF", "SN123");
        verify(orderAdminMapper).update(any(OmsOrder.class), any());
    }

    @Test
    void refund_setsStatus5() {
        when(orderAdminMapper.update(any(), any())).thenReturn(1);
        service.refund(1L);
        verify(orderAdminMapper).update(any(OmsOrder.class), any());
    }

    @Test
    void ship_whenMissing_throws() {
        when(orderAdminMapper.update(any(), any())).thenReturn(0);
        assertThrows(ApiException.class, () -> service.ship(1L, "SF", "SN123"));
    }
}
