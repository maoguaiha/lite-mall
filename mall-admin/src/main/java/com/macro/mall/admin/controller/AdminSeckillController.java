package com.macro.mall.admin.controller;

import com.macro.mall.admin.dto.SeckillProductVO;
import com.macro.mall.admin.dto.SeckillSessionVO;
import com.macro.mall.admin.service.impl.AdminSeckillServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsSeckillSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/seckill")
@Tag(name = "AdminSeckillController", description = "秒杀活动管理")
public class AdminSeckillController {

    private final AdminSeckillServiceImpl service;

    public AdminSeckillController(AdminSeckillServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询秒杀活动")
    @GetMapping("/list")
    public CommonResult<CommonPage<SeckillSessionVO>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增秒杀活动")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody SmsSeckillSession session) {
        service.create(session);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改秒杀活动")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody SmsSeckillSession session) {
        service.update(session);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除秒杀活动")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "查询活动下的秒杀商品")
    @GetMapping("/products")
    public CommonResult<List<SeckillProductVO>> products(@RequestParam Long sessionId) {
        return CommonResult.success(service.products(sessionId));
    }
}
