package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.PmsProductSku;
import com.macro.mall.service.PmsProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "PmsProductController", description = "商品管理")
@RestController
@RequestMapping("/product")
public class PmsProductController {

    private final PmsProductService productService;

    @Autowired
    public PmsProductController(PmsProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "获取商品分类")
    @GetMapping("/category/list")
    public CommonResult<List<PmsProductCategory>> getCategoryList(
            @RequestParam(value = "parentId", defaultValue = "0") Long parentId) {
        List<PmsProductCategory> categoryList = productService.getCategoryList(parentId);
        return CommonResult.success(categoryList);
    }

    @Operation(summary = "获取所有分类")
    @GetMapping("/category/all")
    public CommonResult<List<PmsProductCategory>> getAllCategories() {
        List<PmsProductCategory> categoryList = productService.getAllCategories();
        return CommonResult.success(categoryList);
    }

    @Operation(summary = "获取商品列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<PmsProduct>> getProductList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        CommonPage<PmsProduct> productList = productService.getProductListPage(
                categoryId, keyword, pageNum, pageSize);
        return CommonResult.success(productList);
    }

    @Operation(summary = "获取推荐商品")
    @GetMapping("/recommend")
    public CommonResult<List<PmsProduct>> getRecommendProducts() {
        List<PmsProduct> productList = productService.getRecommendProducts();
        return CommonResult.success(productList);
    }

    @Operation(summary = "获取新品")
    @GetMapping("/new")
    public CommonResult<List<PmsProduct>> getNewProducts() {
        List<PmsProduct> productList = productService.getNewProducts();
        return CommonResult.success(productList);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/detail/{id}")
    public CommonResult<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        PmsProduct product = productService.getProductById(id);
        List<PmsProductSku> skuList = productService.getSkuByProductId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("skuList", skuList);
        return CommonResult.success(result);
    }
}