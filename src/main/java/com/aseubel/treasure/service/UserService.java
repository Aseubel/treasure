package com.aseubel.treasure.service;

import com.aseubel.treasure.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    // 定义用户相关的业务方法，例如注册、登录等
    // IService<User> 已经包含了很多基础的 CRUD 方法
    /**
     * 注册新用户
     * 
     * @param user 包含用户名和原始密码的用户对象
     * @return 注册成功的用户对象 (包含ID)，如果失败则抛出异常或返回 null
     * @throws RuntimeException 如果用户名已存在
     */
    User registerUser(User user) throws RuntimeException;
}