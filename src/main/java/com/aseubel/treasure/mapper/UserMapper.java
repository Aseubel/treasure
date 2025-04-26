package com.aseubel.treasure.mapper;

import com.aseubel.treasure.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// import org.apache.ibatis.annotations.Mapper; // @MapperScan 在配置类中使用了，这里可以省略 @Mapper

// @Mapper // 如果没有使用 @MapperScan，则需要取消此行注释
public interface UserMapper extends BaseMapper<User> {
    // 可以添加自定义的 SQL 方法
}