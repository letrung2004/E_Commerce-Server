package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegisterDTO {
    @NotBlank(message = "Username không được để trống!")
    @Size(max = 45, message = "Username không được quá 45 ký tự!")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống!")
    @Size(min = 3, max = 16, message = "Mật khẩu phải từ 3 đến 16 ký tự!")
    private String password;

    @Size(max = 45, message = "Họ tên không được quá 45 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String fullName;

    @Size(max = 100)
    @Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @Size(max = 11, min = 10, message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String phoneNumber;
}
