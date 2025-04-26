package com.aseubel.treasure.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor; // 导入拦截器
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor; // 导入分页内部拦截器
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean; // 导入 @Bean
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.aseubel.treasure.mapper") // 指定 Mapper 接口所在的包
public class MyBatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页内部拦截器，并指定数据库类型为 MySQL (根据你的实际数据库调整)
        interceptor
                .addInnerInterceptor(new PaginationInnerInterceptor(com.baomidou.mybatisplus.annotation.DbType.MYSQL));
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