package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberAddress;
import com.macro.mall.portal.service.impl.AddressServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/address")
@Tag(name = "AddressController", description = "会员收货地址")
public class AddressController {

    private final AddressServiceImpl addressService;

    public AddressController(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "地址列表")
    @GetMapping("/list")
    public CommonResult<List<UmsMemberAddress>> list() {
        return CommonResult.success(addressService.list());
    }

    @Operation(summary = "地址详情")
    @GetMapping("/{id}")
    public CommonResult<UmsMemberAddress> get(@PathVariable Long id) {
        return CommonResult.success(addressService.get(id));
    }

    @Operation(summary = "新增地址")
    @PostMapping("/create")
    public CommonResult<Void> create(@Valid @RequestBody UmsMemberAddress address) {
        addressService.create(address);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改地址")
    @PostMapping("/update")
    public CommonResult<Void> update(@Valid @RequestBody UmsMemberAddress address) {
        addressService.update(address);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除地址")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        addressService.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "设为默认地址")
    @PostMapping("/default")
    public CommonResult<Void> setDefault(@RequestParam Long id) {
        addressService.setDefault(id);
        return CommonResult.success(null);
    }
}
