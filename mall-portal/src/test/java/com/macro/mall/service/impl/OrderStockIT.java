package com.macro.mall.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.PmsProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存扣减集成测试（@SpringBootTest + H2 内存库）。
 * 验证红线 ①/⑧b：数据库行级条件更新（WHERE stock >= quantity）保证不超卖。
 */
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test_portal;DB_CLOSE_DELAY=-1;MODE=MySQL"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderStockIT {

    @Autowired
    PmsProductService productService;
    @Autowired
    PmsProductMapper productMapper;

    // 正常扣减：库存按数量减少
    @Test
    void deductStock_shouldReduceStock() {
        PmsProduct before = productMapper.selectByPrimaryKey(1L);
        int oldStock = before.getStock();

        productService.deductStock(1L, 5);

        PmsProduct after = productMapper.selectByPrimaryKey(1L);
        assertEquals(oldStock - 5, after.getStock());
    }

    // 库存不足：抛异常且库存不变（行级条件更新影响行数=0）
    @Test
    void deductStock_shouldFail_whenInsufficient() {
        PmsProduct before = productMapper.selectByPrimaryKey(1L);
        int oldStock = before.getStock();

        assertThrows(ApiException.class, () -> productService.deductStock(1L, 999999));

        PmsProduct after = productMapper.selectByPrimaryKey(1L);
        assertEquals(oldStock, after.getStock());
    }
}
