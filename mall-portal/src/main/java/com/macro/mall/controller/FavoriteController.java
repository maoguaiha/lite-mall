package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberFavorite;
import com.macro.mall.service.FavoriteService;
import com.macro.mall.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/favorite")
@Tag(name = "FavoriteController", description = "会员商品收藏")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UmsMemberService memberService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService, UmsMemberService memberService) {
        this.favoriteService = favoriteService;
        this.memberService = memberService;
    }

    @Operation(summary = "收藏商品")
    @PostMapping("/add")
    public CommonResult add(@RequestParam Long productId) {
        Long memberId = memberService.getCurrentMember().getId();
        favoriteService.add(memberId, productId);
        return CommonResult.success(null, "收藏成功");
    }

    @Operation(summary = "取消收藏")
    @PostMapping("/delete")
    public CommonResult delete(@RequestParam Long productId) {
        Long memberId = memberService.getCurrentMember().getId();
        favoriteService.remove(memberId, productId);
        return CommonResult.success(null, "已取消收藏");
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/list")
    public CommonResult<List<UmsMemberFavorite>> list() {
        Long memberId = memberService.getCurrentMember().getId();
        return CommonResult.success(favoriteService.list(memberId));
    }

    @Operation(summary = "是否收藏")
    @GetMapping("/check")
    public CommonResult<Boolean> check(@RequestParam Long productId) {
        Long memberId = memberService.getCurrentMember().getId();
        return CommonResult.success(favoriteService.check(memberId, productId));
    }
}
