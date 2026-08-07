package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.dto.SubmitCommentParam;
import com.macro.mall.mapper.OmsOrderCommentMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.UmsMemberService;
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
class CommentServiceImplTest {

    @Mock
    OmsOrderCommentMapper commentMapper;
    @Mock
    OmsOrderMapper orderMapper;
    @Mock
    UmsMemberService memberService;

    @InjectMocks
    CommentServiceImpl service;

    private SubmitCommentParam param() {
        SubmitCommentParam p = new SubmitCommentParam();
        p.setOrderId(10L);
        p.setProductId(1L);
        p.setStar(5);
        return p;
    }

    private void withMember() {
        UmsMember m = new UmsMember();
        m.setId(1L);
        when(memberService.getCurrentMember()).thenReturn(m);
    }

    @Test
    void submit_ok_whenOrderCompleted() {
        withMember();
        OmsOrder order = new OmsOrder();
        order.setMemberId(1L);
        order.setStatus(4);
        when(orderMapper.selectByPrimaryKey(10L)).thenReturn(order);
        service.submit(param());
        verify(commentMapper).insert(any());
    }

    @Test
    void submit_fails_whenOrderNotCompleted() {
        withMember();
        OmsOrder order = new OmsOrder();
        order.setMemberId(1L);
        order.setStatus(1);
        when(orderMapper.selectByPrimaryKey(10L)).thenReturn(order);
        assertThrows(ApiException.class, () -> service.submit(param()));
    }

    @Test
    void submit_fails_whenOrderNotOwned() {
        withMember();
        OmsOrder order = new OmsOrder();
        order.setMemberId(999L);
        order.setStatus(4);
        when(orderMapper.selectByPrimaryKey(10L)).thenReturn(order);
        assertThrows(ApiException.class, () -> service.submit(param()));
    }
}
