package ua.foxminded.carrestservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.dto.CarDto;

public interface CarService {
	Optional<Car> findById(String id);

	Optional<Car> findOneCar(CarSearchCriteria criteria);

	Page<Car> findAllCarsByCriteria(CarSearchCriteria criteria, Pageable pageable);

	Car createCar(Car car);

	List<Car> createCars(List<Car> cars);

	Car createCar(String manufacturerName, String model, Integer year, List<String> category);

	Car updateCar(CarDto existingCar, CarDto updatedCar);

	void deleteCars(String manufacturer, String model, Integer year);
}
