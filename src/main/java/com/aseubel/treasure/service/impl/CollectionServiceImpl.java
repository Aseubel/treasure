package com.aseubel.treasure.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aseubel.treasure.dto.collection.CollectionDTO;
import com.aseubel.treasure.entity.Collection;
import com.aseubel.treasure.entity.CollectionTag;
import com.aseubel.treasure.entity.Tag;
import com.aseubel.treasure.mapper.CollectionMapper;
import com.aseubel.treasure.mapper.CollectionTagMapper;
import com.aseubel.treasure.mapper.TagMapper;
import com.aseubel.treasure.service.CollectionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    @Autowired
    private CollectionTagMapper collectionTagMapper;

    @Autowired // 注入 CollectionMapper 以便调用自定义方法
    private CollectionMapper collectionMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    @Transactional // 添加事务管理，确保操作的原子性
    public boolean addTagsToCollection(Long collectionId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true; // 没有标签需要添加
        }
        // TODO: 校验 collectionId 是否属于当前用户
        // 校验 collectionId 是否有效，以及是否属于当前用户
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            return false; // 藏品不存在
        }

        // 过滤掉已经存在的关联关系，避免重复插入
        QueryWrapper<CollectionTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("collection_id", collectionId).in("tag_id", tagIds);
        List<Long> existingTagIds = collectionTagMapper.selectList(queryWrapper)
                .stream()
                .map(CollectionTag::getTagId)
                .toList();

        List<CollectionTag> newTags = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId)) // 只添加不存在的关联
                .map(tagId -> {
                    CollectionTag ct = new CollectionTag();
                    ct.setCollectionId(collectionId);
                    ct.setTagId(tagId);
                    ct.setCreatedAt(LocalDateTime.now());
                    return ct;
                }).toList();

        if (!newTags.isEmpty()) {
            collectionTagMapper.insert(newTags);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeTagsFromCollection(Long collectionId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true; // 没有标签需要移除
        }
        // TODO: 校验 collectionId 是否有效，以及是否属于当前用户

        QueryWrapper<CollectionTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("collection_id", collectionId);
        queryWrapper.in("tag_id", tagIds);

        int deletedCount = collectionTagMapper.delete(queryWrapper);
        // 返回删除的记录数是否大于0（或者根据业务需要判断是否等于 tagIds.size()）
        return deletedCount >= 0; // delete 方法返回影响的行数，0表示没有符合条件的记录
    }

    @Override
    public CollectionDTO getCollectionWithTags(Long collectionId) {
        // TODO: 校验 collectionId 是否有效，以及是否属于当前用户
        Collection collection = collectionMapper.selectById(collectionId);
        if (ObjectUtil.isEmpty(collection)) {
            return null; // 藏品不存在
        }
        List<Long> tagIds = collectionTagMapper.selectList(new QueryWrapper<CollectionTag>()
                .eq("collection_id", collectionId))
                .stream()
                .map(CollectionTag::getTagId)
                .toList();
        List<Tag> tags = tagMapper.selectList(new QueryWrapper<Tag>().in("tag_id", tagIds));
        return new CollectionDTO(collection, tags);
    }

    // 如果需要重写删除藏品的方法，以同时删除关联标签
    @Override
    @Transactional
    public boolean removeById(java.io.Serializable id) {
        // 1. 删除 collection_tags 表中的关联记录
        QueryWrapper<CollectionTag> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", id);
        collectionTagMapper.delete(wrapper);

        // 2. 删除 collections 表中的记录
        return super.removeById(id); // 调用父类的删除方法
    }

    @Override
    public IPage<Collection> getCollectionsByPage(Page<Collection> page, Long userId) {
        QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
        // 如果传入了 userId，则添加查询条件
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        // 可以添加其他默认排序条件，例如按创建时间降序
        queryWrapper.orderByDesc("created_at");

        // baseMapper 是 ServiceImpl 中注入的 CollectionMapper
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean removeCollectionTag(Long tagId) {
        collectionTagMapper.delete(new QueryWrapper<CollectionTag>().eq("tag_id", tagId));
        return true;
    }

    // 实现 CollectionService 中定义的其他业务方法
}