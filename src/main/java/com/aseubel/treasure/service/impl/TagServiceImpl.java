package com.aseubel.treasure.service.impl;

import com.aseubel.treasure.entity.Tag;
import com.aseubel.treasure.mapper.TagMapper;
import com.aseubel.treasure.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    // 实现 TagService 中定义的其他业务方法
}