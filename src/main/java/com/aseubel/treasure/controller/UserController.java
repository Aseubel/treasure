package com.aseubel.treasure.controller;

import com.aseubel.treasure.common.JwtUtil; // 导入 JwtUtil
import com.aseubel.treasure.common.Result;
import com.aseubel.treasure.dto.LoginRequest; // 导入 LoginRequest
import com.aseubel.treasure.dto.LoginResponse; // 导入 LoginResponse
import com.aseubel.treasure.entity.User;
import com.aseubel.treasure.service.UserService;
import lombok.extern.slf4j.Slf4j; // 导入 Slf4j
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // 导入 ResponseEntity
import org.springframework.security.authentication.AuthenticationManager; // 导入 AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException; // 导入 BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 导入 UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails; // 导入 UserDetails
import org.springframework.security.core.userdetails.UserDetailsService; // 导入 UserDetailsService
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j // 添加日志注解
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager; // 注入 AuthenticationManager

    @Autowired
    private JwtUtil jwtUtil; // 注入 JwtUtil

    @Autowired
    private UserDetailsService userDetailsService; // 注入 UserDetailsService

    // --- 注册接口 ---
    @PostMapping("/register")
    public Result<User> registerUser(@RequestBody User user) {
        // TODO: 添加更严格的输入校验 (例如使用 @Valid 和 DTO)
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null
                || user.getPassword().isEmpty()) {
            return Result.error(400, "用户名和密码不能为空");
        }
        try {
            User registeredUser = userService.registerUser(user);
            // 注册成功后不返回密码
            registeredUser.setPassword(null);
            return Result.success("注册成功", registeredUser);
        } catch (RuntimeException e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.error(409, e.getMessage()); // 409 Conflict 表示资源冲突（用户名已存在）
        } catch (Exception e) {
            log.error("用户注册时发生未知错误", e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    // --- 登录接口 ---
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            // 使用 AuthenticationManager 进行认证
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn("用户 '{}' 登录失败: 密码错误", loginRequest.getUsername());
            // 返回更友好的错误信息，而不是直接抛出异常
            return ResponseEntity.status(401).body(Result.error(401, "用户名或密码错误"));
            // throw new Exception("用户名或密码错误", e); // 或者直接抛出异常由全局异常处理器处理
        } catch (Exception e) {
            log.error("用户 '{}' 登录时发生错误", loginRequest.getUsername(), e);
            return ResponseEntity.status(500).body(Result.error("登录失败，请稍后重试"));
        }

        // 如果认证成功，加载 UserDetails
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // 生成 JWT
        final String jwt = jwtUtil.generateToken(userDetails);

        // 返回 JWT
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    // === 以下是之前的 CRUD 接口 (现在需要认证才能访问) ===

    // 获取所有用户
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // 示例：添加方法级权限控制，需要配置角色
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.list();
        // 避免返回密码
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    // 根据 ID 获取用户
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        // TODO: 添加权限检查，例如只能获取自己的信息或管理员才能获取
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        } else {
            return Result.error(404, "用户未找到");
        }
    }

    // 创建用户 (注意：实际应用需要处理密码加密) - 这个接口现在意义不大，应使用 /register
    /*
     * @PostMapping
     * public Result<User> createUser(@RequestBody User user) {
     * // 可以在这里添加用户名、邮箱唯一性校验等逻辑
     * boolean success = userService.save(user);
     * if (success) {
     * // 返回创建成功的用户（包含自动生成的 ID）
     * return Result.success("用户创建成功", user);
     * } else {
     * return Result.error("用户创建失败");
     * }
     * }
     */

    // 更新用户
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // TODO: 添加权限检查，例如只能更新自己的信息或管理员才能更新
        // TODO: 处理密码更新逻辑（如果允许更新密码）
        user.setUserId(id); // 确保更新的是指定 ID 的用户
        user.setPassword(null); // 不允许通过此接口直接更新密码原文
        user.setUsername(null); // 通常不允许修改用户名
        // 可以在这里添加校验逻辑
        boolean success = userService.updateById(user);
        if (success) {
            User updatedUser = userService.getById(id); // 获取更新后的信息
            if (updatedUser != null)
                updatedUser.setPassword(null);
            return Result.success("用户更新成功", updatedUser);
        } else {
            // 可能用户不存在或更新失败
            return Result.error(404, "用户更新失败或用户未找到");
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // 示例：只有管理员能删除
    public Result<Void> deleteUser(@PathVariable Long id) {
        // TODO: 添加权限检查
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error(404, "用户删除失败或用户未找到");
        }
    }
}