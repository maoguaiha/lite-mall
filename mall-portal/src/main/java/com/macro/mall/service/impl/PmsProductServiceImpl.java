package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsProductCategoryMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsProductSkuMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.PmsProductSku;
import com.macro.mall.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class PmsProductServiceImpl implements PmsProductService {

    private final PmsProductMapper productMapper;
    private final PmsProductSkuMapper productSkuMapper;
    private final PmsProductCategoryMapper categoryMapper;

    @Autowired
    public PmsProductServiceImpl(PmsProductMapper productMapper, PmsProductSkuMapper productSkuMapper, PmsProductCategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.productSkuMapper = productSkuMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PmsProduct getProductById(Long id) {
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            Asserts.fail("商品不存在");
        }
        return product;
    }

    @Override
    public List<PmsProductSku> getSkuByProductId(Long productId) {
        return productSkuMapper.selectByProductId(productId);
    }

    @Override
    public List<PmsProductCategory> getCategoryList(Long parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<PmsProductCategory> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<PmsProduct> getProductList(Long categoryId, String keyword) {
        if (categoryId != null && keyword != null && !keyword.isEmpty()) {
            return productMapper.selectByCategoryIdsAndKeyword(collectCategoryIds(categoryId), keyword);
        } else if (categoryId != null) {
            return productMapper.selectByCategoryIds(collectCategoryIds(categoryId));
        } else if (keyword != null && !keyword.isEmpty()) {
            return productMapper.selectByKeyword(keyword);
        } else {
            return productMapper.selectRecommendProducts();
        }
    }

    /**
     * 递归收集分类及其所有子孙分类的 id，使父分类筛选能命中挂在子分类下的商品。
     */
    private List<Long> collectCategoryIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(categoryId);
        while (!queue.isEmpty()) {
            Long cur = queue.poll();
            ids.add(cur);
            List<PmsProductCategory> children = categoryMapper.selectByParentId(cur);
            if (children != null) {
                for (PmsProductCategory child : children) {
                    queue.add(child.getId());
                }
            }
        }
        return ids;
    }

    @Override
    public List<PmsProduct> getRecommendProducts() {
        return productMapper.selectRecommendProducts();
    }

    @Override
    public List<PmsProduct> getNewProducts() {
        return productMapper.selectNewProducts();
    }

    @Override
    public CommonPage<PmsProduct> getProductListPage(Long categoryId, String keyword,
                                                     Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProduct> productList = getProductList(categoryId, keyword);
        return CommonPage.restPage(productList);
    }

    @Override
    public void deductStock(Long productId, Integer quantity) {
        // 原子扣减：由数据库行级条件更新保证不超卖（WHERE stock >= quantity），
        // 替代原先「先查后改」的 TOCTOU 竞态（红线 ①/⑧b）。
        // 影响行数为 0 表示商品不存在或库存不足，由事务回滚保证订单与库存一致。
        int affected = productMapper.decrementStock(productId, quantity);
        if (affected == 0) {
            Asserts.fail("库存不足或商品不存在");
        }
    }
}