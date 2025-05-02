package com.aseubel.treasure.dto.tag;

import com.aseubel.treasure.entity.Tag;
import lombok.Data;

/**
 * @author Aseubel
 * @date 2025/5/2 下午2:47
 */
@Data
public class UpdateTagRequest {

    private Long tagId;

    private String tagName;

    private String description;

    public Tag toEntity() {
        Tag tag = new Tag();
        tag.setTagId(tagId);
        tag.setTagName(tagName);
        tag.setDescription(description);
        return tag;
    }
}
