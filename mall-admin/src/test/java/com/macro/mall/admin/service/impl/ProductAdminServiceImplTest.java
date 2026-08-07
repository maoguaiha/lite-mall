package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.PmsProductAdminMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
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
class ProductAdminServiceImplTest {

    @Mock
    PmsProductAdminMapper productAdminMapper;
    @Mock
    PmsProductMapper productMapper;

    @InjectMocks
    ProductAdminServiceImpl service;

    @Test
    void list_returnsPaged() {
        when(productAdminMapper.selectList(any())).thenReturn(Collections.singletonList(new PmsProduct()));
        assertNotNull(service.list(1, 10, null));
        verify(productAdminMapper).selectList(any());
    }

    @Test
    void create_inserts() {
        service.create(new PmsProduct());
        verify(productAdminMapper).insert(any(PmsProduct.class));
    }

    @Test
    void delete_isLogicDelete_notPhysical() {
        service.delete(1L);
        verify(productAdminMapper).update(any(PmsProduct.class), any());
        verify(productAdminMapper, never()).deleteById(anyLong());
    }
}
