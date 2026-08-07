package com.macro.mall.admin.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.mapper.UmsAdminRoleAdminMapper;
import com.macro.mall.mapper.UmsRoleAdminMapper;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.model.UmsAdminRole;
import com.macro.mall.model.UmsRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    private final UmsAdminMapper adminMapper;
    private final UmsAdminRoleAdminMapper adminRoleMapper;
    private final UmsRoleAdminMapper roleMapper;

    public AdminUserDetailsServiceImpl(UmsAdminMapper adminMapper,
                                        UmsAdminRoleAdminMapper adminRoleMapper,
                                        UmsRoleAdminMapper roleMapper) {
        this.adminMapper = adminMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UmsAdmin admin = adminMapper.selectOne(
                new QueryWrapper<UmsAdmin>().eq("username", username).eq("delete_flag", 0));
        if (admin == null) {
            throw new UsernameNotFoundException("管理员不存在");
        }
        return User.withUsername(admin.getUsername())
                .password(admin.getPassword())
                .authorities(resolveAuthorities(admin.getId()))
                .build();
    }

    private List<GrantedAuthority> resolveAuthorities(Long adminId) {
        List<UmsAdminRole> links = adminRoleMapper.selectList(new QueryWrapper<UmsAdminRole>().eq("admin_id", adminId));
        if (links.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        Set<String> codes = new LinkedHashSet<>();
        for (UmsAdminRole link : links) {
            UmsRole role = roleMapper.selectById(link.getRoleId());
            if (role != null && StringUtils.hasText(role.getCode())) {
                codes.add(role.getCode());
            }
        }
        if (codes.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return codes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
