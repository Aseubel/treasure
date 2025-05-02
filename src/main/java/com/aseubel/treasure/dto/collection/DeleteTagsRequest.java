package com.aseubel.treasure.dto.collection;

import lombok.Data;

import java.util.List;

/**
 * @author Aseubel
 * @date 2025/5/2 下午2:30
 */
@Data
public class DeleteTagsRequest {

    private Long collectionId;

    private List<Long> tagIds;
}
