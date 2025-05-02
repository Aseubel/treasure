package com.aseubel.treasure.service.impl;

import com.aseubel.treasure.entity.User;
import com.aseubel.treasure.mapper.UserMapper;
import com.aseubel.treasure.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入密码编码器

    @Override
    @Transactional // 确保注册操作的原子性
    public User registerUser(User user) throws RuntimeException {
        // 1. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (baseMapper.exists(queryWrapper)) {
            throw new RuntimeException("用户名 '" + user.getUsername() + "' 已被注册");
        }

        // 2. 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 设置创建时间等默认值
        user.setCreatedAt(LocalDateTime.now());
        // user.setUpdatedAt(LocalDateTime.now()); // 如果有更新时间字段

        // 4. 保存用户
        boolean success = this.save(user); // 使用 ServiceImpl 提供的 save 方法
        if (!success) {
            // 理论上如果前面校验通过，这里不应该失败，但以防万一
            throw new RuntimeException("用户注册失败，请稍后重试");
        }

        // 返回保存后的用户对象（包含自动生成的 ID）
        return user;
    }

    @Override
    public Long getUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return baseMapper.selectOne(new QueryWrapper<User>().eq("username", username)).getUserId();
    }
}