package com.shopsphere.order.service;

import java.math.BigDecimal;

public interface PaymentService {

    String processPayment(String userEmail, BigDecimal amount);
}
