package com.macro.mall.admin.runner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.UmsAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 播种默认商家管理员（避免把 bcrypt 哈希写死在 SQL 中）。
 * 账号 admin / admin123
 */
@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final UmsAdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(UmsAdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        long count = adminMapper.selectCount(new QueryWrapper<UmsAdmin>().eq("delete_flag", 0));
        if (count == 0) {
            UmsAdmin admin = new UmsAdmin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("超级管理员");
            admin.setRole("ADMIN");
            admin.setDeleteFlag(0);
            adminMapper.insert(admin);
        }
    }
}
