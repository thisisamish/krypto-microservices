package com.groupeight.order_service.web.exception;

public class InsufficientStockException extends RuntimeException {
	public InsufficientStockException(String message) {
		super(message);
	}
}
