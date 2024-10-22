package ua.foxminded.carrestservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import ua.foxminded.carrestservice.exception.car.CarAlreadyExistsException;
import ua.foxminded.carrestservice.exception.car.CarNotFoundException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerAlreadyExistsException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerInvalidException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {


	@ExceptionHandler(ManufacturerAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleManufacturerAlreadyExists(ManufacturerAlreadyExistsException ex,
			WebRequest request) {
		return buildResponseEntity(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
	}

	@ExceptionHandler(ManufacturerInvalidException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidManufacturer(ManufacturerInvalidException ex,
			WebRequest request) {
		return buildResponseEntity(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler(ManufacturerNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleManufacturerNotFound(ManufacturerNotFoundException ex,
			WebRequest request) {
		return buildResponseEntity(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	@ExceptionHandler(CarNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCarNotFoundException(CarNotFoundException ex, WebRequest request) {
		return buildResponseEntity(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	@ExceptionHandler(CarAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleCarAlreadyExistsException(CarAlreadyExistsException ex,
			WebRequest request) {
		return buildResponseEntity(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getFieldErrors()
	        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

	    Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", LocalDateTime.now());
	    body.put("status", HttpStatus.BAD_REQUEST.value());
	    body.put("error", "Validation Failed");
	    body.put("message", "Invalid input");
	    body.put("errors", errors);

	    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException ex, WebRequest request) {
		return buildResponseEntity(HttpStatus.BAD_REQUEST, "Bad Request",
				String.format("The required parameter '%s' is missing.", ex.getParameterName()), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
		return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
				"An unexpected error occurred.", request);
	}

	private ResponseEntity<Map<String, Object>> buildResponseEntity(HttpStatus status, String error, String message,
			WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", error);
		body.put("message", message);
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, status);
	}
}
