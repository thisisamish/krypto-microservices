package com.groupeight.order_service.application;

import java.math.BigDecimal;

import com.groupeight.order_service.domain.PaymentMethod;

public interface PaymentService {
	record PaymentResult(boolean success, String reference, String message) {
	}

	PaymentResult charge(PaymentMethod method, BigDecimal amount);
}
