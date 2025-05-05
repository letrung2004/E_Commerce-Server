package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Payment;
import com.ecom.webapp.repository.PaymentRepository;
import com.ecom.webapp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public void markPaymentAsPaid(String transactionId) {
        List<Payment> payments = this.paymentRepository.findPaymentsByTransactionId(transactionId);
        for (Payment payment : payments) {
            payment.setStatus("Đã thanh toán");
            this.paymentRepository.updatePayment(payment);
        }
    }
}
