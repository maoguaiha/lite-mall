package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsProductCategoryAdminMapper;
import com.macro.mall.model.PmsProductCategory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class CategoryAdminServiceImpl {

    private final PmsProductCategoryAdminMapper categoryMapper;

    public CategoryAdminServiceImpl(PmsProductCategoryAdminMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public CommonPage<PmsProductCategory> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<PmsProductCategory> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        qw.orderByAsc("parent_id").orderByAsc("sort").orderByDesc("id");
        return CommonPage.restPage(categoryMapper.selectList(qw));
    }

    public List<PmsProductCategory> options() {
        QueryWrapper<PmsProductCategory> qw = new QueryWrapper<>();
        qw.eq("show_status", 1).orderByAsc("parent_id").orderByAsc("sort");
        return categoryMapper.selectList(qw);
    }

    public void create(PmsProductCategory category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        category.setLevel(category.getParentId() == 0 ? 1 : 2);
        if (category.getShowStatus() == null) {
            category.setShowStatus(1);
        }
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        categoryMapper.insert(category);
    }

    public void update(PmsProductCategory category) {
        if (category.getId() == null) {
            Asserts.fail("分类ID不能为空");
        }
        PmsProductCategory existing = categoryMapper.selectById(category.getId());
        if (existing == null) {
            Asserts.fail("分类不存在");
        }
        if (category.getParentId() == null) {
            category.setParentId(existing.getParentId());
        }
        category.setLevel(category.getParentId() == 0 ? 1 : 2);
        category.setUpdateTime(new Date());
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        PmsProductCategory existing = categoryMapper.selectById(id);
        if (existing == null) {
            Asserts.fail("分类不存在");
        }
        if (categoryMapper.selectCount(new QueryWrapper<PmsProductCategory>().eq("parent_id", id)) > 0) {
            Asserts.fail("该分类下存在子分类，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
