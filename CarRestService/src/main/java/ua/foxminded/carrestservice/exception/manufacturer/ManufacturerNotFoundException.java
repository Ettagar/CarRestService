package ua.foxminded.carrestservice.exception.manufacturer;

@SuppressWarnings("serial")
public class ManufacturerNotFoundException extends RuntimeException {
    public ManufacturerNotFoundException(String message) {
        super(message);
    }
}
