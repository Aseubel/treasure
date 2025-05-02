package com.aseubel.treasure.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Aseubel
 * @date 2025/5/2 下午6:27
 */
@Data
public class UpdateUserRequest implements Serializable {

    private String email;
}
