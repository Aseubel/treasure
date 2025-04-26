package com.aseubel.treasure.config;

import com.aseubel.treasure.common.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // 确保导入正确的类

import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter { // 继承 OncePerRequestFilter

    @Autowired
    private UserDetailsService userDetailsService; // 注入 UserDetailsService

    @Autowired
    private JwtUtil jwtUtil; // 注入 JwtUtil

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 检查 Header 是否存在且以 "Bearer " 开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // 提取 JWT
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                log.warn("无法从令牌获取用户名", e);
            } catch (ExpiredJwtException e) {
                log.warn("令牌已过期", e);
                // 可以根据需要在这里处理过期逻辑，例如返回特定的错误响应
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // response.getWriter().write("Token expired");
                // return;
            } catch (Exception e) {
                log.error("解析令牌时出错", e);
            }
        } else {
            // log.trace("Authorization header 不存在或格式不正确"); // 可以取消注释用于调试
        }

        // 如果获取到用户名且当前 SecurityContext 中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证 token 是否有效
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // 第三个参数是权限
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息设置到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                log.debug("用户 '{}' 认证成功，设置 Security Context", username);
            } else {
                log.warn("令牌无效: {}", jwt);
            }
        } else {
            // log.trace("用户名为空或 SecurityContext 已包含认证信息"); // 可以取消注释用于调试
        }

        // 继续执行过滤器链中的下一个过滤器
        chain.doFilter(request, response);
    }
}