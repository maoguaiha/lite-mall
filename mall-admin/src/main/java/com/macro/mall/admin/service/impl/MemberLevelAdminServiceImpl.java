package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberLevelAdminMapper;
import com.macro.mall.model.UmsMemberLevel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MemberLevelAdminServiceImpl {

    private final UmsMemberLevelAdminMapper levelMapper;

    public MemberLevelAdminServiceImpl(UmsMemberLevelAdminMapper levelMapper) {
        this.levelMapper = levelMapper;
    }

    public CommonPage<UmsMemberLevel> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<UmsMemberLevel> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        qw.orderByAsc("id");
        return CommonPage.restPage(levelMapper.selectList(qw));
    }

    public void create(UmsMemberLevel level) {
        if (level.getDefaultStatus() == null) {
            level.setDefaultStatus(0);
        }
        levelMapper.insert(level);
    }

    public void update(UmsMemberLevel level) {
        if (level.getId() == null) {
            Asserts.fail("会员等级ID不能为空");
        }
        levelMapper.updateById(level);
    }

    public void delete(Long id) {
        levelMapper.deleteById(id);
    }
}
