package com.macro.mall.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.PmsProductCategoryMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsProductSkuMapper;
import com.macro.mall.model.PmsProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商品模块单元测试（Mockito，纯逻辑）。
 * 覆盖详情查询、库存原子扣减（①/⑧b）、列表分页与多分支路由。
 */
@ExtendWith(MockitoExtension.class)
class PmsProductServiceTest {

    @Mock
    PmsProductMapper productMapper;
    @Mock
    PmsProductSkuMapper productSkuMapper;
    @Mock
    PmsProductCategoryMapper categoryMapper;

    @InjectMocks
    PmsProductServiceImpl productService;

    // 详情：存在 -> 返回
    @Test
    void getProductById_shouldReturn_whenExists() {
        PmsProduct p = new PmsProduct();
        p.setId(1L);
        when(productMapper.selectByPrimaryKey(1L)).thenReturn(p);
        assertEquals(1L, productService.getProductById(1L).getId());
    }

    // 详情：不存在 -> 抛异常
    @Test
    void getProductById_shouldFail_whenMissing() {
        when(productMapper.selectByPrimaryKey(99L)).thenReturn(null);
        assertThrows(ApiException.class, () -> productService.getProductById(99L));
    }

    // 扣库存：影响行数>0 -> 调用 decrementStock
    @Test
    void deductStock_shouldCallMapper() {
        when(productMapper.decrementStock(1L, 5)).thenReturn(1);
        productService.deductStock(1L, 5);
        verify(productMapper).decrementStock(1L, 5);
    }

    // 扣库存：影响行数=0（库存不足/商品不存在）-> 抛异常（行级条件更新保证不超卖）
    @Test
    void deductStock_shouldFail_whenNoAffectedRows() {
        when(productMapper.decrementStock(1L, 999999)).thenReturn(0);
        assertThrows(ApiException.class, () -> productService.deductStock(1L, 999999));
    }

    // 分页：按分类查询走 selectByCategoryId
    @Test
    void getProductListPage_shouldPageByCategory() {
        when(productMapper.selectByCategoryId(2L)).thenReturn(List.of(new PmsProduct()));
        productService.getProductListPage(2L, null, 1, 10);
        verify(productMapper).selectByCategoryId(2L);
    }

    // 列表路由：categoryId+keyword / categoryId / keyword / 默认推荐 四分支
    @Test
    void getProductList_shouldRouteBranches() {
        productService.getProductList(2L, "iphone");
        verify(productMapper).selectByCategoryIdAndKeyword(2L, "iphone");

        productService.getProductList(2L, null);
        verify(productMapper).selectByCategoryId(2L);

        productService.getProductList(null, "iphone");
        verify(productMapper).selectByKeyword("iphone");

        productService.getProductList(null, null);
        verify(productMapper).selectRecommendProducts();
    }
}
