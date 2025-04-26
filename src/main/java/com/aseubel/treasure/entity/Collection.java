package com.aseubel.treasure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("collections")
public class Collection {
    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;
    private Long userId;
    private String name;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String type;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}