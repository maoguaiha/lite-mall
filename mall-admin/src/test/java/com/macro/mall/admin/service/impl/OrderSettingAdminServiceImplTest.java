package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.OmsOrderSettingAdminMapper;
import com.macro.mall.model.OmsOrderSetting;
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
class OrderSettingAdminServiceImplTest {

    @Mock
    OmsOrderSettingAdminMapper settingMapper;
    @InjectMocks
    OrderSettingAdminServiceImpl service;

    @Test
    void get_whenMissing_createsDefault() {
        when(settingMapper.selectById(1L)).thenReturn(null);
        OmsOrderSetting setting = service.get();
        assertEquals(1L, setting.getId());
        verify(settingMapper).insert(any());
    }

    @Test
    void update_whenExists_updates() {
        when(settingMapper.selectById(1L)).thenReturn(new OmsOrderSetting());
        OmsOrderSetting incoming = new OmsOrderSetting();
        incoming.setConfirmOvertime(7);
        service.update(incoming);
        assertEquals(1L, incoming.getId());
        verify(settingMapper).updateById(incoming);
    }
}
