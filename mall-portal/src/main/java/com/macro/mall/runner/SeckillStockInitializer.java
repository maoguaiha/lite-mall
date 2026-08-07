package com.macro.mall.portal.runner;

import com.macro.mall.mapper.SmsSeckillProductMapper;
import com.macro.mall.model.SmsSeckillProduct;
import com.macro.mall.portal.service.impl.SeckillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动后将秒杀库存预热到 Redis（best-effort：Redis 不可用时仅告警，不阻断启动）。
 */
@Component
public class SeckillStockInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeckillStockInitializer.class);

    private final SmsSeckillProductMapper seckillProductMapper;
    private final SeckillServiceImpl seckillService;

    public SeckillStockInitializer(SmsSeckillProductMapper seckillProductMapper, SeckillServiceImpl seckillService) {
        this.seckillProductMapper = seckillProductMapper;
        this.seckillService = seckillService;
    }

    @Override
    public void run(String... args) {
        try {
            List<SmsSeckillProduct> products = seckillProductMapper.selectList(null);
            for (SmsSeckillProduct product : products) {
                seckillService.initStock(product.getId());
            }
            log.info("秒杀库存预热完成，共 {} 个秒杀商品", products.size());
        } catch (Exception e) {
            log.warn("秒杀库存预热失败（Redis 可能未启动），可稍后调用 /seckill/init 手动预热: {}", e.getMessage());
        }
    }
}
