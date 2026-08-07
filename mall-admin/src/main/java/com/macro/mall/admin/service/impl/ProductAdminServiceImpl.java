package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsProductAdminMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductAdminServiceImpl {

    private final PmsProductAdminMapper productAdminMapper;
    private final PmsProductMapper productMapper;

    public ProductAdminServiceImpl(PmsProductAdminMapper productAdminMapper, PmsProductMapper productMapper) {
        this.productAdminMapper = productAdminMapper;
        this.productMapper = productMapper;
    }

    public CommonPage<PmsProduct> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<PmsProduct> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        qw.orderByDesc("id");
        return CommonPage.restPage(productAdminMapper.selectList(qw));
    }

    public PmsProduct get(Long id) {
        PmsProduct product = productAdminMapper.selectById(id);
        if (product == null) {
            Asserts.fail("商品不存在");
        }
        return product;
    }

    public void create(PmsProduct product) {
        if (product.getPublishStatus() == null) {
            product.setPublishStatus(1);
        }
        productAdminMapper.insert(product);
    }

    public void update(PmsProduct product) {
        if (product.getId() == null) {
            Asserts.fail("商品ID不能为空");
        }
        productAdminMapper.updateById(product);
    }

    public void updatePublishStatus(Long id, Integer publishStatus) {
        productAdminMapper.update(new PmsProduct(),
                new UpdateWrapper<PmsProduct>().eq("id", id).set("publish_status", publishStatus));
    }

    public void updateStock(Long id, Integer stock) {
        productMapper.updateStock(id, stock);
    }

    public void delete(Long id) {
        productAdminMapper.update(new PmsProduct(),
                new UpdateWrapper<PmsProduct>().eq("id", id).set("delete_flag", 2));
    }
}
