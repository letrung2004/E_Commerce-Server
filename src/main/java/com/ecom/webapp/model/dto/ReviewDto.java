package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDto {
    private int id;

    private int userId;

    private String username;

    @NotNull(message = "Không được bỏ trống mục này!")
    private int productId;

    private String comment;

    @NotNull(message = "Không được bỏ trống mục này!")
    private int rate;
}
