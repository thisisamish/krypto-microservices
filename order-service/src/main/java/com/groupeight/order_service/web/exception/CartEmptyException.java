package com.groupeight.order_service.web.exception;

public class CartEmptyException extends RuntimeException {
	public CartEmptyException(String message) {
		super(message);
	}
}
