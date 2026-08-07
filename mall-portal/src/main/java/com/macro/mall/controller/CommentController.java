package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.SubmitCommentParam;
import com.macro.mall.model.OmsOrderComment;
import com.macro.mall.portal.service.impl.CommentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "CommentController", description = "订单评价")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "提交评价")
    @PostMapping("/member/comment/submit")
    public CommonResult<Void> submit(@Valid @RequestBody SubmitCommentParam param) {
        commentService.submit(param);
        return CommonResult.success(null);
    }

    @Operation(summary = "我的评价")
    @GetMapping("/member/comment/my")
    public CommonResult<CommonPage<OmsOrderComment>> my(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(commentService.myList(pageNum, pageSize));
    }

    @Operation(summary = "商品评价列表（公开）")
    @GetMapping("/product/comment/list")
    public CommonResult<CommonPage<OmsOrderComment>> productList(@RequestParam Long productId,
                                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(commentService.productList(productId, pageNum, pageSize));
    }
}
