package com.groupeight.order_service.web.exception;

public class PaymentFailedException extends RuntimeException {
	public PaymentFailedException(String message) {
		super(message);
	}
}
