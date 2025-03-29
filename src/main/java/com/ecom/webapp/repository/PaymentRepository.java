package com.ecom.webapp.repository;

import com.ecom.webapp.model.Payment;

public interface PaymentRepository {
    Payment getPaymentById(int id);
    void createPayment(Payment payment);
    void updatePayment(Payment payment);
}
