package com.macro.mall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 独立密码编码器配置，避免与 SecurityConfig 形成循环依赖
 * （SecurityConfig -> JwtAuthenticationFilter -> memberService -> PasswordEncoder -> SecurityConfig）。
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
