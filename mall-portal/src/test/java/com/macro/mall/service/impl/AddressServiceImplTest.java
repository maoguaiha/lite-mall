package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.UmsMemberAddressMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberAddress;
import com.macro.mall.service.UmsMemberService;
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
class AddressServiceImplTest {

    @Mock
    UmsMemberAddressMapper addressMapper;
    @Mock
    UmsMemberService memberService;

    @InjectMocks
    AddressServiceImpl service;

    private void withMember() {
        UmsMember m = new UmsMember();
        m.setId(1L);
        when(memberService.getCurrentMember()).thenReturn(m);
    }

    @Test
    void create_setsMemberId() {
        withMember();
        UmsMemberAddress a = new UmsMemberAddress();
        service.create(a);
        assertEquals(1L, a.getMemberId());
        verify(addressMapper).insert(any(UmsMemberAddress.class));
    }

    @Test
    void delete_isLogicDelete() {
        withMember();
        UmsMemberAddress existing = new UmsMemberAddress();
        existing.setId(5L);
        existing.setMemberId(1L);
        when(addressMapper.selectById(5L)).thenReturn(existing);
        service.delete(5L);
        verify(addressMapper).update(argThat(x -> x instanceof UmsMemberAddress), any());
        verify(addressMapper, never()).deleteById(anyLong());
    }

    @Test
    void setDefault_resetsOthers() {
        withMember();
        UmsMemberAddress existing = new UmsMemberAddress();
        existing.setId(5L);
        existing.setMemberId(1L);
        when(addressMapper.selectById(5L)).thenReturn(existing);
        service.setDefault(5L);
        verify(addressMapper, times(2)).update(any(UmsMemberAddress.class), any());
    }
}
