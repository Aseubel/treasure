package com.aseubel.treasure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@MapperScan("com.aseubel.treasure.mapper") // 指定 Mapper 接口所在的包
public class MyBatisPlusConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DbType dbType = switch (activeProfile) {
            case "dev" -> DbType.MYSQL;
            case "pro" -> DbType.ORACLE;
            default -> DbType.MYSQL;
        };
        // 添加分页内部拦截器
        interceptor
                .addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return interceptor;
    }

    /**
     * 配置密码编码器 Bean
     * 
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}