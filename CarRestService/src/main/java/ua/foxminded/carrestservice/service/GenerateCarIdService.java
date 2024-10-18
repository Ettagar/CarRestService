package ua.foxminded.carrestservice.service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.repository.CarRepository;

@Service
@RequiredArgsConstructor
public class GenerateCarIdService {
	private final CarRepository carRepository;

	public String generateUniquetId() {
		String newObjectId;
		boolean collision;
		do {
			newObjectId = generateId();
			collision = checkForCollision(newObjectId);
		} while (collision);
		return newObjectId;
	}

	private String generateId() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());

		String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
		return encoded.substring(0, 10);
	}

	private boolean checkForCollision(String carId) {
		Optional<Car> existingCar = carRepository.findById(carId);
		return existingCar.isPresent();
	}
}
