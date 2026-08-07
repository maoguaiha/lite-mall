package com.macro.mall.admin.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.SmsHomeAdvertiseAdminMapper;
import com.macro.mall.model.SmsHomeAdvertise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdvertiseAdminServiceImplTest {

    @Mock
    SmsHomeAdvertiseAdminMapper advertiseMapper;
    @InjectMocks
    AdvertiseAdminServiceImpl service;

    @Test
    void create_setsDefaultsAndInserts() {
        SmsHomeAdvertise ad = new SmsHomeAdvertise();
        ad.setName("banner");
        service.create(ad);
        assertNotNull(ad.getCreateTime());
        assertEquals(0, ad.getDeleteFlag());
        verify(advertiseMapper).insert(ad);
    }

    @Test
    void update_withoutId_fails() {
        SmsHomeAdvertise ad = new SmsHomeAdvertise();
        assertThrows(ApiException.class, () -> service.update(ad));
    }

    @Test
    void delete_callsDeleteById() {
        service.delete(3L);
        verify(advertiseMapper).deleteById(3L);
    }

    @Test
    void list_returnsData() {
        when(advertiseMapper.selectList(any())).thenReturn(Collections.emptyList());
        assertNotNull(service.list(1, 10, null));
    }
}
