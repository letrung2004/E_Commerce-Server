package com.ecom.webapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "store")
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Size(max = 50, message = "Tên cửa hàng không được quá 50 ký tự!")
    @Size(min = 2, message = "Tên cửa hàng không được ít hơn 2 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "logo")
    private String logo;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @ColumnDefault("1")
    @Column(name = "active")
    private boolean active = true;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = true, unique = true)
    private com.ecom.webapp.model.User owner;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Category> categories;

}