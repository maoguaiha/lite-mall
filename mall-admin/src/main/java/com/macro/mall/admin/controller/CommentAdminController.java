package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.CommentAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrderComment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
@Tag(name = "CommentAdminController", description = "评价管理")
public class CommentAdminController {

    private final CommentAdminServiceImpl service;

    public CommentAdminController(CommentAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "评价分页列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<OmsOrderComment>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestParam(required = false) Integer status) {
        return CommonResult.success(service.list(pageNum, pageSize, status));
    }

    @Operation(summary = "评价详情")
    @GetMapping("/{id}")
    public CommonResult<OmsOrderComment> get(@PathVariable Long id) {
        return CommonResult.success(service.get(id));
    }

    @Operation(summary = "审核评价")
    @PostMapping("/audit")
    public CommonResult<Void> audit(@RequestParam Long id,
                                    @RequestParam Integer status,
                                    @RequestParam(required = false) String reply) {
        service.audit(id, status, reply);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除评价（逻辑删）")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
