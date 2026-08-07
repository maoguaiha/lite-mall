package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.SmsSeckillProductMapper;
import com.macro.mall.model.SmsSeckillProduct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillServiceImpl {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> seckillScript;
    private final SmsSeckillProductMapper seckillProductMapper;
    private final SeckillOrderServiceImpl seckillOrderService;

    public SeckillServiceImpl(StringRedisTemplate redisTemplate, SmsSeckillProductMapper seckillProductMapper,
                              SeckillOrderServiceImpl seckillOrderService) {
        this.redisTemplate = redisTemplate;
        this.seckillScript = RedisScript.of(new ClassPathResource("seckill.lua"), Long.class);
        this.seckillProductMapper = seckillProductMapper;
        this.seckillOrderService = seckillOrderService;
    }

    public void initStock(Long seckillProductId) {
        SmsSeckillProduct sp = seckillProductMapper.selectById(seckillProductId);
        if (sp == null) {
            Asserts.fail("秒杀商品不存在");
        }
        redisTemplate.delete(userKey(seckillProductId));
        redisTemplate.opsForValue().set(stockKey(seckillProductId), String.valueOf(sp.getSeckillStock()));
    }

    /**
     * 秒杀入口：Redis Lua 原子扣减（不包裹 @Transactional），成功后再落库下单。
     */
    public Long seckill(Long memberId, Long seckillProductId) {
        Long result = redisTemplate.execute(seckillScript,
                List.of(stockKey(seckillProductId), userKey(seckillProductId)),
                String.valueOf(memberId));
        if (result == null) {
            Asserts.fail("秒杀系统异常");
        }
        if (result == -1) {
            Asserts.fail("秒杀尚未开始或库存未预热");
        }
        if (result == 0) {
            Asserts.fail("已售罄");
        }
        if (result == 2) {
            Asserts.fail("您已参与过该秒杀");
        }
        return seckillOrderService.createSeckillOrder(memberId, seckillProductId);
    }

    public List<SmsSeckillProduct> list() {
        return seckillProductMapper.selectList(null);
    }

    private String stockKey(Long id) {
        return "seckill:stock:" + id;
    }

    private String userKey(Long id) {
        return "seckill:users:" + id;
    }
}
