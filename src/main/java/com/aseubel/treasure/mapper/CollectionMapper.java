package com.aseubel.treasure.mapper;

import com.aseubel.treasure.dto.collection.CollectionDTO;
import com.aseubel.treasure.entity.Collection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CollectionMapper extends BaseMapper<Collection> {

    /**
     * 根据藏品ID查询藏品及其关联的标签信息
     * 
     * @param collectionId 藏品ID
     * @return CollectionDTO 包含标签列表
     */
    // 注意：这里的 SQL 是一个示例，需要根据你的数据库结构和 MyBatis 的 ResultMap 配置来精确实现
    // 推荐使用 MyBatis XML 文件来编写复杂的关联查询 SQL
    @Select("""
            SELECT
                c.*,
                t.tag_id as tags_tag_id,
                t.tag_name as tags_tag_name,
                t.description as tags_description,
                t.created_at as tags_created_at,
                t.user_id as tags_user_id
            FROM
                collections c
            LEFT JOIN
                collection_tags ct ON c.collection_id = ct.collection_id
            LEFT JOIN
                tags t ON ct.tag_id = t.tag_id
            WHERE
                c.collection_id = #{collectionId}
            """)
    CollectionDTO findCollectionWithTagsById(@Param("collectionId") Long collectionId);

    // 如果需要查询某个用户的所有藏品及其标签，可以添加类似方法
    // List<CollectionDTO> findUserCollectionsWithTags(@Param("userId") Long
    // userId);
}