package com.macro.mall.admin.controller;

import com.macro.mall.admin.service.impl.SubjectAdminServiceImpl;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsSubject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/subject")
@Tag(name = "SubjectAdminController", description = "专题管理")
public class SubjectAdminController {

    private final SubjectAdminServiceImpl service;

    public SubjectAdminController(SubjectAdminServiceImpl service) {
        this.service = service;
    }

    @Operation(summary = "分页查询专题")
    @GetMapping("/list")
    public CommonResult<CommonPage<CmsSubject>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return CommonResult.success(service.list(pageNum, pageSize, keyword));
    }

    @Operation(summary = "新增专题")
    @PostMapping("/create")
    public CommonResult<Void> create(@RequestBody CmsSubject subject) {
        service.create(subject);
        return CommonResult.success(null);
    }

    @Operation(summary = "修改专题")
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody CmsSubject subject) {
        service.update(subject);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除专题")
    @PostMapping("/delete")
    public CommonResult<Void> delete(@RequestParam Long id) {
        service.delete(id);
        return CommonResult.success(null);
    }
}
