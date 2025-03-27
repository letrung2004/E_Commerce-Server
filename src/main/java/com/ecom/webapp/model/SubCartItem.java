package com.ecom.webapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "sub_cart_item")
public class SubCartItem implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sub_cart_id", nullable = false)
    private SubCart subCart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ColumnDefault("1")
    @Column(name = "quantity")
    private Integer quantity;

    @ColumnDefault("0")
    @Column(name = "unit_price", precision = 10)
    private BigDecimal unitPrice;

}