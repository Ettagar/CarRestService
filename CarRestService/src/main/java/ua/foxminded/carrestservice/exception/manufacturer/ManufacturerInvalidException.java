package ua.foxminded.carrestservice.exception.manufacturer;

@SuppressWarnings("serial")
public class ManufacturerInvalidException extends RuntimeException {
    public ManufacturerInvalidException(String message) {
        super(message);
    }
}
