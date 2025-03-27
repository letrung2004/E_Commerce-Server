package com.ecom.webapp.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    private int id;
    @Size(max = 45, message = "Họ tên không được quá 45 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String fullName;

    @Size(max = 100)
    @Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @Size(max = 11, min = 10, message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String phoneNumber;

    private boolean gender;
    private LocalDate dateOfBirth;
    private String role;


}
