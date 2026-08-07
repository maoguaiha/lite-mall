package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsCartItem;
import com.macro.mall.service.CmsCartService;
import com.macro.mall.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CmsCartController", description = "购物车管理")
@RestController
@RequestMapping("/cart")
public class CmsCartController {

    private final CmsCartService cartService;
    private final UmsMemberService memberService;

    @Autowired
    public CmsCartController(CmsCartService cartService, UmsMemberService memberService) {
        this.cartService = cartService;
        this.memberService = memberService;
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public CommonResult<List<CmsCartItem>> getCartList() {
        Long memberId = memberService.getCurrentMember().getId();
        List<CmsCartItem> cartList = cartService.getCartList(memberId);
        return CommonResult.success(cartList);
    }

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public CommonResult<CmsCartItem> addCart(
            @RequestParam Long productId,
            @RequestParam(value = "productSkuId", required = false) Long productSkuId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity) {
        Long memberId = memberService.getCurrentMember().getId();
        CmsCartItem cartItem = cartService.addCart(memberId, productId, productSkuId, quantity);
        return CommonResult.success(cartItem, "添加成功");
    }

    @Operation(summary = "更新购物车商品数量")
    @PostMapping("/update/{id}")
    public CommonResult updateCartItem(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Long memberId = memberService.getCurrentMember().getId();
        cartService.updateCartItem(memberId, id, quantity);
        return CommonResult.success(null, "更新成功");
    }

    @Operation(summary = "删除购物车商品")
    @PostMapping("/delete/{id}")
    public CommonResult deleteCartItem(@PathVariable Long id) {
        Long memberId = memberService.getCurrentMember().getId();
        cartService.deleteCartItem(memberId, id);
        return CommonResult.success(null, "删除成功");
    }

    @Operation(summary = "批量删除购物车商品")
    @PostMapping("/delete/batch")
    public CommonResult deleteCartItems(@RequestBody List<Long> ids) {
        Long memberId = memberService.getCurrentMember().getId();
        cartService.deleteCartItems(memberId, ids);
        return CommonResult.success(null, "删除成功");
    }

    @Operation(summary = "清空购物车")
    @PostMapping("/clear")
    public CommonResult clearCart() {
        Long memberId = memberService.getCurrentMember().getId();
        cartService.clearCart(memberId);
        return CommonResult.success(null, "清空成功");
    }
}