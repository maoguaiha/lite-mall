package com.macro.mall.admin.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.CmsSubjectAdminMapper;
import com.macro.mall.model.CmsSubject;
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
class SubjectAdminServiceImplTest {

    @Mock
    CmsSubjectAdminMapper subjectMapper;
    @InjectMocks
    SubjectAdminServiceImpl service;

    @Test
    void create_setsDefaultsAndInserts() {
        CmsSubject subject = new CmsSubject();
        subject.setTitle("topic");
        service.create(subject);
        assertNotNull(subject.getCreateTime());
        assertEquals(0, subject.getDeleteFlag());
        verify(subjectMapper).insert(subject);
    }

    @Test
    void update_withoutId_fails() {
        CmsSubject subject = new CmsSubject();
        assertThrows(ApiException.class, () -> service.update(subject));
    }

    @Test
    void delete_callsDeleteById() {
        service.delete(3L);
        verify(subjectMapper).deleteById(3L);
    }

    @Test
    void list_returnsData() {
        when(subjectMapper.selectList(any())).thenReturn(Collections.emptyList());
        assertNotNull(service.list(1, 10, null));
    }
}
