package com.macro.mall.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.PmsProductSku;

import java.util.List;

public interface PmsProductService {
    PmsProduct getProductById(Long id);
    
    List<PmsProductSku> getSkuByProductId(Long productId);
    
    List<PmsProductCategory> getCategoryList(Long parentId);
    
    List<PmsProductCategory> getAllCategories();
    
    List<PmsProduct> getProductList(Long categoryId, String keyword);
    
    List<PmsProduct> getRecommendProducts();
    
    List<PmsProduct> getNewProducts();
    
    CommonPage<PmsProduct> getProductListPage(Long categoryId, String keyword, 
                                               Integer pageNum, Integer pageSize);
    
    void deductStock(Long productId, Integer quantity);
}