package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberAdminMapper;
import com.macro.mall.model.UmsMember;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MemberAdminServiceImpl {

    private final UmsMemberAdminMapper memberAdminMapper;

    public MemberAdminServiceImpl(UmsMemberAdminMapper memberAdminMapper) {
        this.memberAdminMapper = memberAdminMapper;
    }

    public CommonPage<UmsMember> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UmsMember> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("username", keyword).or().like("nickname", keyword));
        }
        qw.orderByDesc("id");
        return CommonPage.restPage(memberAdminMapper.selectList(qw));
    }

    public UmsMember get(Long id) {
        UmsMember member = memberAdminMapper.selectById(id);
        if (member == null) {
            Asserts.fail("会员不存在");
        }
        return member;
    }

    public void updateStatus(Long id, Integer status) {
        memberAdminMapper.update(new UmsMember(),
                new UpdateWrapper<UmsMember>().eq("id", id).set("status", status));
    }

    public void delete(Long id) {
        // 会员不做物理删除，统一置为禁用（status=0）作为逻辑删除
        memberAdminMapper.update(new UmsMember(),
                new UpdateWrapper<UmsMember>().eq("id", id).set("status", 0));
    }
}
