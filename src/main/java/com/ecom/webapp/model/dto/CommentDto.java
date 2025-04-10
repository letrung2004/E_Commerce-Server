package com.ecom.webapp.model.dto;

import com.ecom.webapp.model.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private int id;
    private int userId;

    @Size(max = 255, message = "Nội dung không được quá 255 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String content;

    @NotNull(message = "Không được bỏ trống mục này!")
    private int commentParentId;
}
