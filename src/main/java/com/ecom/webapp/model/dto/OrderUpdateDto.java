package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderUpdateDto {
    @NotNull(message = "Không được bỏ trống trường này!")
    private int id;
    private String status;
}
