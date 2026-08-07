package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.SmsSeckillProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SeckillServiceImplTest {

    @Mock
    StringRedisTemplate redisTemplate;
    @Mock
    SmsSeckillProductMapper seckillProductMapper;
    @Mock
    SeckillOrderServiceImpl seckillOrderService;

    @InjectMocks
    SeckillServiceImpl service;

    @Test
    void seckill_success_returnsOrderId() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any())).thenReturn(1L);
        when(seckillOrderService.createSeckillOrder(1L, 1L)).thenReturn(99L);
        assertEquals(99L, service.seckill(1L, 1L));
    }

    @Test
    void seckill_soldOut_throws() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any())).thenReturn(0L);
        assertThrows(ApiException.class, () -> service.seckill(1L, 1L));
    }

    @Test
    void seckill_alreadyParticipated_throws() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any())).thenReturn(2L);
        assertThrows(ApiException.class, () -> service.seckill(1L, 1L));
    }
}
