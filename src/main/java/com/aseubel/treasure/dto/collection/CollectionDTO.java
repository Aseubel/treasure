package com.aseubel.treasure.dto.collection;

import com.aseubel.treasure.entity.Collection;
import com.aseubel.treasure.entity.Tag;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // 如果需要继承 Collection 的 equals/hashCode
public class CollectionDTO extends Collection { // 继承自 Collection 实体类，包含其所有字段
    private List<Tag> tags; // 添加一个标签列表字段

    public CollectionDTO(Collection collection, List<Tag> tags) {
        super(collection.getCollectionId(), collection.getUserId(), collection.getName(),
                collection.getPurchaseDate(), collection.getPurchasePrice(), collection.getType(),
                collection.getNotes(), collection.getCreatedAt(), collection.getUpdatedAt());
        this.tags = tags;
    }
}