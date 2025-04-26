package com.aseubel.treasure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 启用方法级安全注解
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // 导入 AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder; // 确保导入 PasswordEncoder
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration; // 如果需要 CORS
// import org.springframework.web.cors.CorsConfigurationSource; // 如果需要 CORS
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 如果需要 CORS
// import java.util.Arrays; // 如果需要 CORS

@Configuration
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true) // 启用方法级别的安全注解，如 @PreAuthorize
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // 注入 JWT 过滤器

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // 注入 UserDetailsService 实现

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入之前配置的密码编码器

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 防护，因为我们使用 JWT
                .csrf(AbstractHttpConfigurer::disable)
                // 配置 URL 权限
                .authorizeHttpRequests(authz -> authz
                        // 允许匿名访问登录和注册接口 (需要稍后在 UserController 中创建)
                        .requestMatchers("/users/login", "/users/register").permitAll()
                        // 允许匿名访问 Swagger/OpenAPI 文档（如果使用）
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                // 配置 Session 管理策略为无状态 (STATELESS)，因为我们不依赖 Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 将 JWT 过滤器添加到 Spring Security 过滤器链中
                // 在 UsernamePasswordAuthenticationFilter 之前执行
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        // 如果需要 CORS 配置，可以在这里添加 .cors(cors ->
        // cors.configurationSource(corsConfigurationSource()))

        return http.build();
    }

    /**
     * 暴露 AuthenticationManager Bean，用于登录认证
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // --- 可选：CORS 配置 ---
    /*
     * @Bean
     * public CorsConfigurationSource corsConfigurationSource() {
     * CorsConfiguration configuration = new CorsConfiguration();
     * configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000",
     * "http://yourfrontenddomain.com")); // 允许的前端来源
     * configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
     * "OPTIONS"));
     * configuration.setAllowedHeaders(Arrays.asList("Authorization",
     * "Cache-Control", "Content-Type"));
     * configuration.setAllowCredentials(true); // 允许携带凭证
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * source.registerCorsConfiguration("/**", configuration); // 对所有路径应用配置
     * return source;
     * }
     */

}