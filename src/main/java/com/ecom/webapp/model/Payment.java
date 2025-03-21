package com.ecom.webapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private com.ecom.webapp.model.User user;

    @Size(max = 45)
    @Column(name = "payment_method", length = 45)
    private String paymentMethod;

    @Size(max = 45)
    @Column(name = "status", length = 45)
    private String status;

    @Column(name = "amount", precision = 10)
    private BigDecimal amount;

    @Column(name = "date_created")
    private Instant dateCreated;

}