package ua.foxminded.carrestservice.exception.car;

@SuppressWarnings("serial")
public class CarAlreadyExistsException extends RuntimeException {
	public CarAlreadyExistsException(String message) {
		super(message);
	}
}
