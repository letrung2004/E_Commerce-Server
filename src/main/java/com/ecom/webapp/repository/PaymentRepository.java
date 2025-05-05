package com.ecom.webapp.repository;

import com.ecom.webapp.model.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment getPaymentById(int id);
    List<Payment> findPaymentsByTransactionId(String transactionId);
    void createPayment(Payment payment);
    void updatePayment(Payment payment);


}
