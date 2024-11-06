package ua.foxminded.carrestservice.util;

import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.CarDto;

@Getter
public class ApplicationTestData {
	private final ManufacturerTestData manufacturerTestData = new ManufacturerTestData();
	private final CategoriesTestData categoriesTestData = new CategoriesTestData();
	private final CarTestData carTestData = new CarTestData(manufacturerTestData, categoriesTestData);

	private List<Car> cars;
	private List<CarDto> carDtos;
	private List<Manufacturer> manufacturers;
	private List<Category> categories;

	public void setUp() {
		manufacturerTestData.setUp();
		manufacturers = manufacturerTestData.getManufacturers();

		categoriesTestData.setUp();
		categories = categoriesTestData.getCategories();

		carTestData.setUp();
		cars = carTestData.getCars();
		carDtos = carTestData.getCarDtos();
	}
}
