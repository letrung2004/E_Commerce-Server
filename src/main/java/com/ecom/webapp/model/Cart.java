package com.ecom.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class Cart implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private com.ecom.webapp.model.User user;

    @ColumnDefault("0")
    @Column(name = "items_number")
    private Integer itemsNumber;


    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }
    //Composition
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubCart> subCarts;
}