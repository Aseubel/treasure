package com.aseubel.treasure.dto.tag;

import com.aseubel.treasure.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Aseubel
 * @date 2025/5/2 下午2:37
 */
@Data
public class CreateTagRequest {

    private String tagName;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    public Tag toEntity() {
        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setCreatedAt(createdAt);
        tag.setDescription(description);
        return tag;
    }
}
