package com.macro.mall.admin.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.OmsOrderCommentMapper;
import com.macro.mall.model.OmsOrderComment;
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
class CommentAdminServiceImplTest {

    @Mock
    OmsOrderCommentMapper commentMapper;

    @InjectMocks
    CommentAdminServiceImpl service;

    @Test
    void audit_pass_setsStatus2() {
        service.audit(1L, 2, "good");
        verify(commentMapper).update(any(OmsOrderComment.class), any());
    }

    @Test
    void audit_invalidStatus_throws() {
        assertThrows(ApiException.class, () -> service.audit(1L, 9, "x"));
    }

    @Test
    void delete_isLogicDelete() {
        service.delete(1L);
        verify(commentMapper).update(any(OmsOrderComment.class), any());
        verify(commentMapper, never()).deleteById(anyLong());
    }
}
