package com.aseubel.treasure.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 建议将密钥和过期时间配置在 application.yml 中
    @Value("${jwt.secret:defaultSecretKeyWhichIsLongEnoughForHS256}") // 从配置文件读取密钥，提供默认值
    private String secret;

    @Value("${jwt.expiration:86400000}") // 从配置文件读取过期时间（毫秒），默认为 1 天
    private long expiration;

    private SecretKey getSigningKey() {
        // 使用 Keys.hmacShaKeyFor 生成安全的密钥
        // 注意：每次重启应用，如果 secret 相同，生成的 key 也是相同的
        // 更好的做法是将生成的密钥持久化存储或使用更健壮的密钥管理
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 从 token 中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从 token 中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从 token 中提取指定的 claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析 token 获取所有 claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查 token 是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 为指定用户生成 token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 可以在 claims 中添加用户的角色、权限等信息
        // claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    // 创建 token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // subject 通常存储用户名
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用 HS256 签名算法
                .compact();
    }

    // 验证 token 是否有效
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // 检查用户名是否匹配且 token 未过期
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}