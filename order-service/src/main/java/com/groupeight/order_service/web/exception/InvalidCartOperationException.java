package com.groupeight.order_service.web.exception;

public class InvalidCartOperationException extends RuntimeException {
	public InvalidCartOperationException(String message) {
		super(message);
	}
}
