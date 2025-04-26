package com.aseubel.treasure.dto;

import com.aseubel.treasure.entity.Collection;
import com.aseubel.treasure.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true) // 如果需要继承 Collection 的 equals/hashCode
public class CollectionDTO extends Collection { // 继承自 Collection 实体类，包含其所有字段
    private List<Tag> tags; // 添加一个标签列表字段
}