package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.dto.CarDto;

@RequiredArgsConstructor
@Getter
public class CarTestData {
	private final ManufacturerTestData manufacturerTestData;
	private final CategoriesTestData categoriesTestData;

	private List<Car> cars;
	private List<CarDto> carDtos;

	public void setUp() {

		Car car1 = Car.builder()
				.id("123456789A")
				.manufacturer(manufacturerTestData.getManufacturers().get(0))
				.model("Q3")
				.year(2020)
				.categories(Arrays.asList(categoriesTestData.getCategories().get(0)))
				.build();

		Car car2 = Car.builder()
				.id("123456789B")
				.manufacturer(manufacturerTestData.getManufacturers().get(1))
				.model("CX-5")
				.year(2022)
				.categories(Arrays.asList(categoriesTestData.getCategories().get(0)))
				.build();

		Car car3 = Car.builder()
				.id("123456789C")
				.manufacturer(manufacturerTestData.getManufacturers().get(2))
				.model("Jetta")
				.year(2024)
				.categories(Arrays.asList(categoriesTestData.getCategories().get(1)))
				.build();

		Car car4 = Car.builder()
				.id("123456789D")
				.manufacturer(manufacturerTestData.getManufacturers().get(0))
				.model("A6")
				.year(2024)
				.categories(Arrays.asList(categoriesTestData.getCategories().get(1)))
				.build();

		cars = Arrays.asList(car1, car2, car3, car4);

		carDtos = cars.stream()
				.map(car -> new CarDto(
						car.getId(),
						car.getManufacturer().getName(),
						car.getModel(), car.getYear(),
						car.getCategories().stream()
						.map(Category::getName)
						.toList()))
				.toList();
	}
}
