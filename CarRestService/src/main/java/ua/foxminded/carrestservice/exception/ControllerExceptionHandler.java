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
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.CONFLICT.value());
		body.put("error", "Conflict");
		body.put("message", ex.getMessage());
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ManufacturerInvalidException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidManufacturer(ManufacturerInvalidException ex,
			WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Bad Request");
		body.put("message", ex.getMessage());
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ManufacturerNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleManufacturerDoesNotExists(ManufacturerNotFoundException ex,
			WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "Not Found");
		body.put("message", ex.getMessage());
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CarNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCarNotFoundException(CarNotFoundException ex, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "Not Found");
		body.put("message", ex.getMessage());
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CarAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicateCarException(CarAlreadyExistsException ex,
			WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.CONFLICT.value());
		body.put("error", "Conflict");
		body.put("message", ex.getMessage());
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
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
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Bad Request");
		body.put("message", String.format("The required parameter '%s' is missing.", ex.getParameterName()));
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "Internal Server Error");
		body.put("message", "An unexpected error occurred.");
		body.put("path", request.getDescription(false));

		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
