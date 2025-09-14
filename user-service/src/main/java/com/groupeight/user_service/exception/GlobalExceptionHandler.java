package com.groupeight.user_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.groupeight.user_service.web.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		return ResponseEntity.status(status).body(
				new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI()));
	}

	// --- Fallback: 500 ---
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(status).body(
				new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI()));
	}

	// --- Security: 401 / 403 (optional but nice for consistency) ---
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		return ResponseEntity.status(status).body(
				new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI()));
	}

	// --- 404: Not Found ---
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(status).body(
				new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI()));
	}

	// --- 409: Conflict (user already exists, stock issues, etc.) ---
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		return ResponseEntity.status(status).body(
				new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI()));
	}

	// --- Bean Validation (@Valid) ---
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach((FieldError fe) -> errors.put(fe.getField(), fe.getDefaultMessage()));

		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(),
				"Validation failed", request.getRequestURI(), errors));
	}
}
