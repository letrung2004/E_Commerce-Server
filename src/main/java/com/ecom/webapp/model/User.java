package com.ecom.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45, message = "Họ tên không được quá 45 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    @Column(name = "full_name", nullable = false, length = 45)
    private String fullName;

    @Size(max = 45, message = "Username không được quá 45 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Size(max = 255, min = 3, message = "Mật khẩu tối thiểu 3 và tối đa 16 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Size(max = 100)
    @Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 11, min = 10, message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Không được bỏ trống mục này!")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @ColumnDefault("1")
    @Column(name = "gender")
    private boolean gender;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @ColumnDefault("1")
    @Column(name = "active")
    private boolean active;

    @ColumnDefault("0")
    @Column(name = "store_active")
    private boolean storeActive;

    @Size(max = 45)
    @Column(name = "role", length = 45)
    private String role;

    @Size(max = 255)
    @Column(name = "avatar")
    private String avatar;

    // Xoa user thi ko xoa store (Neu user co store nhung chua duyet thi xoa ca 2, nguoc lai store.setOwner=null)
    @OneToOne(mappedBy = "owner")
    private Store store;

    // Xoa User thi xoa luon addresses casacade=ALL va orphanRemoval = true
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Address> address;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @Transient
    private MultipartFile file;

    @Override
    public String toString() {
        return String.format("%d, %s, %s, %s", this.id, this.fullName, this.email, this.phoneNumber);
    }
}