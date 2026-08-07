package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.OmsOrderAdminMapper;
import com.macro.mall.mapper.OmsOrderCommentMapper;
import com.macro.mall.mapper.PmsProductAdminMapper;
import com.macro.mall.mapper.UmsMemberAdminMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardAdminServiceImplTest {

    @Mock
    OmsOrderAdminMapper orderAdminMapper;
    @Mock
    UmsMemberAdminMapper memberAdminMapper;
    @Mock
    PmsProductAdminMapper productAdminMapper;
    @Mock
    OmsOrderCommentMapper commentMapper;

    @InjectMocks
    DashboardAdminServiceImpl service;

    @Test
    void getStats_returnsExpectedKeys() {
        when(orderAdminMapper.selectCount(any())).thenReturn(5L);
        when(memberAdminMapper.selectCount(any())).thenReturn(10L);
        when(productAdminMapper.selectCount(any())).thenReturn(3L);
        when(orderAdminMapper.selectTotalSales()).thenReturn(new BigDecimal("1234.00"));
        when(commentMapper.selectCount(any())).thenReturn(2L);

        Map<String, Object> stats = service.getStats();
        assertEquals(5L, stats.get("orderCount"));
        assertEquals(10L, stats.get("memberCount"));
        assertEquals(3L, stats.get("productCount"));
        assertEquals(new BigDecimal("1234.00"), stats.get("totalSales"));
        assertEquals(2L, stats.get("commentPending"));
    }
}
