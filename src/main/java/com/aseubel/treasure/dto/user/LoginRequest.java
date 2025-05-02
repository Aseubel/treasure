package com.aseubel.treasure.dto.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String username; // 或者 email，取决于登录方式
    private String password;
}