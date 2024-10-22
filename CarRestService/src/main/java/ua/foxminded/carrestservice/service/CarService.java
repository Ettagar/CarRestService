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

	Optional<CarDto> findOneCar(CarSearchCriteria criteria);

	Page<CarDto> findAllCarsByCriteria(CarSearchCriteria criteria, Pageable pageable);

	Car createCar(Car car);

	List<CarDto> createCars(List<Car> cars);

	CarDto createCar(String manufacturerName, String model, Integer year, List<String> category);

	CarDto updateCar(CarDto existingCar, CarDto updatedCar);

	void deleteCars(String manufacturer, String model, Integer year);
}
