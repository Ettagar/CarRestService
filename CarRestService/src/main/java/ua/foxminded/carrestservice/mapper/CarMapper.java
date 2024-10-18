package ua.foxminded.carrestservice.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarMappingContext;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.dto.CarDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarMapper {
	public CarDto toDto(Car car) {
		return new CarDto(
				car.getId(),
				car.getManufacturer().getName(),
				car.getModel(),
				car.getYear(),
				car.getCategories().stream()
					.map(Category :: getName)
					.collect(Collectors.toList())
		);
	}

	public List<CarDto> toDto(List<Car> cars) {
		return cars.stream()
				.map(this :: toDto)
				.collect(Collectors.toList());
	}

	public Car toModel(CarDto carDto, CarMappingContext context) {
		return new Car(
		carDto.id(),
		context.getManufacturer(),
		carDto.model(),
		carDto.year(),
		context.getCategories()
		);
	}
}
