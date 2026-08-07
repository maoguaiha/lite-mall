package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.UmsMemberAdminMapper;
import com.macro.mall.model.UmsMember;
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
class MemberAdminServiceImplTest {

    @Mock
    UmsMemberAdminMapper memberAdminMapper;

    @InjectMocks
    MemberAdminServiceImpl service;

    @Test
    void list_returnsMembers() {
        when(memberAdminMapper.selectList(any())).thenReturn(java.util.Collections.singletonList(new UmsMember()));
        assertNotNull(service.list(1, 10, null));
    }

    @Test
    void delete_disablesMember_notPhysical() {
        service.delete(1L);
        verify(memberAdminMapper).update(any(UmsMember.class), any());
        verify(memberAdminMapper, never()).deleteById(anyLong());
    }
}
