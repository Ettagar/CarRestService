package ua.foxminded.carrestservice.exception;

@SuppressWarnings("serial")
public class ServiceException extends Exception {

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String string, String message) {
		super(string + message);
	}
}
