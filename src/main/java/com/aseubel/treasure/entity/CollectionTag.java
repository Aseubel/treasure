package com.aseubel.treasure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("collection_tags")
public class CollectionTag {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long collectionId;
    private Long tagId;
    private LocalDateTime createdAt;
}