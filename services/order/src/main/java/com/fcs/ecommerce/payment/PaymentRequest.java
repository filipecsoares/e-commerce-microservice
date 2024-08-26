package com.fcs.ecommerce.payment;

import com.fcs.ecommerce.customer.CustomerResponse;
import com.fcs.ecommerce.order.PaymentMethod;
import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Long orderId,
        String orderReference,
        CustomerResponse customer
) {
}
