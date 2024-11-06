package ua.foxminded.carrestservice.exception.car;

@SuppressWarnings("serial")
public class CarNotFoundException extends RuntimeException{
	public CarNotFoundException(String message) {
		super(message);
	}
}
