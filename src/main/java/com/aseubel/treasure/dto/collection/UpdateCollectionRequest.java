package com.aseubel.treasure.dto.collection;

import com.aseubel.treasure.entity.Collection;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Aseubel
 * @date 2025/5/2 下午2:17
 */
@Data
public class UpdateCollectionRequest {

    private Long collectionId;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private String type;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    public Collection convertToEntity() {
        return Collection.builder()
                .collectionId(collectionId)
                .name(name)
                .purchaseDate(purchaseDate)
                .purchasePrice(purchasePrice)
                .type(type)
                .notes(notes)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }
}
