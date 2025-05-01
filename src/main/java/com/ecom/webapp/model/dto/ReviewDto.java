package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ReviewDto {
    private int id;

    private int userId;

    private String username;

    @NotNull(message = "Không được bỏ trống mục này!")
    private List<Integer> orderDetailIds;

    private List<String> comments;

    @NotNull(message = "Không được bỏ trống mục này!")
    private List<Integer> rates;
}
