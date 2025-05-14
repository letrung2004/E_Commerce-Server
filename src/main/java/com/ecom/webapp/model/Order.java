package com.ecom.webapp.model;

import com.ecom.webapp.model.dto.OrderDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "`order`")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private com.ecom.webapp.model.User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "shipping_fee", precision = 10)
    private BigDecimal shippingFee;

    @Column(name = "total", precision = 10)
    private BigDecimal total;

    @Size(max = 45)
    @Column(name = "delivery_status", length = 45)
    private String deliveryStatus;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    @Size(max = 45)
    @Column(name = "payment_method", length = 45)
    private String paymentMethod;

    //Composition
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private Payment payment;


    @Column(name = "date_created")
    private Instant dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Size(max = 45)
    @Column(name = "uuid_key", length = 45)
    private String uuidKey;


    @PrePersist
    protected void onCreate() {
        this.dateCreated = Instant.now();
    }

}