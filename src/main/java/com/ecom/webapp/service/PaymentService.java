package com.ecom.webapp.service;

import java.util.List;

public interface PaymentService {
    void markPaymentAsPaid(String transactionId);
}
