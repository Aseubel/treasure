package com.aseubel.treasure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT
    // 可以添加用户信息等其他需要返回的数据
    // private UserDTO user;
}