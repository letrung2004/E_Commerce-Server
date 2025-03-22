package com.ecom.webapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 45)
    private String fullName;

    @Size(max = 45)
    @NotNull
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Size(max = 100)
    @NotNull
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 15)
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @ColumnDefault("1")
    @Column(name = "gender")
    private Byte gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ColumnDefault("1")
    @Column(name = "active")
    private Byte active;

    @ColumnDefault("0")
    @Column(name = "store_active")
    private Byte storeActive;

    @Size(max = 45)
    @Column(name = "role", length = 45)
    private String role;

    @Size(max = 255)
    @Column(name = "avatar")
    private String avatar;

}