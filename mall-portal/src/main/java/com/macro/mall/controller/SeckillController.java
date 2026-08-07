package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsSeckillProduct;
import com.macro.mall.portal.service.impl.SeckillServiceImpl;
import com.macro.mall.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seckill")
@Tag(name = "SeckillController", description = "秒杀（Redis+Lua 原子扣减）")
public class SeckillController {

    private final SeckillServiceImpl seckillService;
    private final UmsMemberService memberService;

    public SeckillController(SeckillServiceImpl seckillService, UmsMemberService memberService) {
        this.seckillService = seckillService;
        this.memberService = memberService;
    }

    @Operation(summary = "秒杀商品列表")
    @GetMapping("/list")
    public CommonResult<List<SmsSeckillProduct>> list() {
        return CommonResult.success(seckillService.list());
    }

    @Operation(summary = "初始化库存到 Redis（运营/测试调用）")
    @PostMapping("/init")
    public CommonResult<Void> init(@RequestParam Long seckillProductId) {
        seckillService.initStock(seckillProductId);
        return CommonResult.success(null);
    }

    @Operation(summary = "参与秒杀")
    @PostMapping("/buy")
    public CommonResult<Long> buy(@RequestParam Long seckillProductId) {
        Long orderId = seckillService.seckill(memberService.getCurrentMember().getId(), seckillProductId);
        return CommonResult.success(orderId);
    }
}
