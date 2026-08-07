package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.admin.dto.AdminInfo;
import com.macro.mall.admin.security.AdminJwtTokenUtil;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.mapper.UmsAdminRoleAdminMapper;
import com.macro.mall.mapper.UmsRoleAdminMapper;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.model.UmsAdminRole;
import com.macro.mall.model.UmsRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminLoginServiceImpl {

    private final UmsAdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminJwtTokenUtil jwtTokenUtil;
    private final UmsAdminRoleAdminMapper adminRoleMapper;
    private final UmsRoleAdminMapper roleMapper;

    public AdminLoginServiceImpl(UmsAdminMapper adminMapper, PasswordEncoder passwordEncoder,
                                 AdminJwtTokenUtil jwtTokenUtil,
                                 UmsAdminRoleAdminMapper adminRoleMapper,
                                 UmsRoleAdminMapper roleMapper) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.adminRoleMapper = adminRoleMapper;
        this.roleMapper = roleMapper;
    }

    public String login(String username, String password) {
        UmsAdmin admin = adminMapper.selectOne(
                new QueryWrapper<UmsAdmin>().eq("username", username).eq("delete_flag", 0));
        if (admin == null) {
            Asserts.fail("管理员不存在");
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            Asserts.fail("密码错误");
        }
        return jwtTokenUtil.generateToken(admin.getUsername());
    }

    public AdminInfo getAdminInfo(String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UmsAdmin admin = adminMapper.selectOne(
                new QueryWrapper<UmsAdmin>().eq("username", username).eq("delete_flag", 0));
        AdminInfo info = new AdminInfo();
        info.setUsername(admin != null ? admin.getUsername() : username);
        info.setRoles(resolveRoles(admin != null ? admin.getId() : null));
        return info;
    }

    private List<String> resolveRoles(Long adminId) {
        if (adminId == null) {
            return List.of("ROLE_ADMIN");
        }
        List<UmsAdminRole> links = adminRoleMapper.selectList(new QueryWrapper<UmsAdminRole>().eq("admin_id", adminId));
        if (links.isEmpty()) {
            return List.of("ROLE_ADMIN");
        }
        Set<String> codes = new LinkedHashSet<>();
        for (UmsAdminRole link : links) {
            UmsRole role = roleMapper.selectById(link.getRoleId());
            if (role != null && StringUtils.hasText(role.getCode())) {
                codes.add(role.getCode());
            }
        }
        return codes.isEmpty() ? List.of("ROLE_ADMIN") : new ArrayList<>(codes);
    }
}
