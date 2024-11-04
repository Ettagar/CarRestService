package ua.foxminded.carrestservice.exception.manufacturer;

@SuppressWarnings("serial")
public class ManufacturerAlreadyExistsException extends RuntimeException {
	public ManufacturerAlreadyExistsException(String message) {
		super(message);
	}
}
