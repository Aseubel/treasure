package com.aseubel.treasure.service;

import com.aseubel.treasure.dto.collection.CollectionDTO;
import com.aseubel.treasure.entity.Collection;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CollectionService extends IService<Collection> {
    // 定义藏品相关的业务方法

    /**
     * 为藏品添加标签
     * 
     * @param collectionId 藏品ID
     * @param tagIds       标签ID列表
     * @return 是否成功
     */
    boolean addTagsToCollection(Long collectionId, List<Long> tagIds);

    /**
     * 移除藏品的标签
     * 
     * @param collectionId 藏品ID
     * @param tagIds       标签ID列表
     * @return 是否成功
     */
    boolean removeTagsFromCollection(Long collectionId, List<Long> tagIds);

    /**
     * 获取带有标签信息的藏品详情
     * 
     * @param collectionId 藏品ID
     * @return CollectionDTO 或 null
     */
    CollectionDTO getCollectionWithTags(Long collectionId);

    /**
     * 分页获取藏品列表
     * 
     * @param page   分页参数对象 (包含页码 pageNum 和每页大小 pageSize)
     * @param userId (可选) 用户ID，用于过滤
     * @return 分页结果 IPage<Collection>
     */
    IPage<Collection> getCollectionsByPage(Page<Collection> page, Long userId); // 可以根据需要决定是否传入 userId

    /**
     * 移除藏品标签
     *
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean removeCollectionTag(Long tagId);
    // 可以添加更新藏品标签的方法（先移除旧的，再添加新的）
    // boolean updateCollectionTags(Long collectionId, List<Long> tagIds);
}