package com.aseubel.treasure.service.impl;

import com.aseubel.treasure.entity.User;
import com.aseubel.treasure.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // 用于创建空的权限列表

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper; // 注入 UserMapper 来查询数据库

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username); // 或者根据 email 查询，取决于你的登录方式
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 返回 Spring Security 的 UserDetails 对象
        // 第三个参数是权限列表，这里暂时使用空列表，后续可以根据用户角色进行填充
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // 数据库中存储的应该是加密后的密码
                new ArrayList<>() // 权限列表，例如：Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}