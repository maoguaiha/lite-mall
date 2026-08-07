package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.UmsAdmin;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class AdminUserServiceImpl {

    private final UmsAdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UmsAdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public CommonPage<UmsAdmin> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<UmsAdmin> qw = new QueryWrapper<>();
        qw.eq("delete_flag", 0);
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("username", keyword).or().like("nickname", keyword));
        }
        qw.orderByDesc("id");
        return CommonPage.restPage(adminMapper.selectList(qw));
    }

    public void create(UmsAdmin admin) {
        if (!StringUtils.hasText(admin.getUsername()) || !StringUtils.hasText(admin.getPassword())) {
            Asserts.fail("用户名和密码不能为空");
        }
        if (adminMapper.selectCount(new QueryWrapper<UmsAdmin>().eq("username", admin.getUsername())) > 0) {
            Asserts.fail("用户名已存在");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        if (admin.getRole() == null) {
            admin.setRole("admin");
        }
        if (admin.getDeleteFlag() == null) {
            admin.setDeleteFlag(0);
        }
        admin.setCreateTime(new Date());
        adminMapper.insert(admin);
    }

    public void update(UmsAdmin admin) {
        if (admin.getId() == null) {
            Asserts.fail("管理员ID不能为空");
        }
        UmsAdmin existing = adminMapper.selectById(admin.getId());
        if (existing == null || (existing.getDeleteFlag() != null && existing.getDeleteFlag() == 2)) {
            Asserts.fail("管理员不存在");
        }
        if (StringUtils.hasText(admin.getPassword())) {
            existing.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        if (StringUtils.hasText(admin.getNickname())) {
            existing.setNickname(admin.getNickname());
        }
        if (StringUtils.hasText(admin.getRole())) {
            existing.setRole(admin.getRole());
        }
        adminMapper.updateById(existing);
    }

    public void delete(Long id) {
        UmsAdmin existing = adminMapper.selectById(id);
        if (existing == null) {
            Asserts.fail("管理员不存在");
        }
        adminMapper.deleteById(id);
    }

    public List<UmsAdmin> all() {
        return adminMapper.selectList(new QueryWrapper<UmsAdmin>().eq("delete_flag", 0));
    }
}
