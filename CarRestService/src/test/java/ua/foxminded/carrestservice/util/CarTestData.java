package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.foxminded.carrestservice.model.Car;

@RequiredArgsConstructor
@Getter
public class CarTestData {
	private final ManufacturerTestData manufacturerTestData;
	private final CategoriesTestData categoriesTestData;

	private List<Car> cars;

	public void setUp() {

		Car car1 = new Car();
		car1.setId("123456789A");
		car1.setManufacturer(manufacturerTestData.getManufacturers().get(0));
		car1.setModel("Q3");
		car1.setYear(2020);
		car1.setCategories(Arrays.asList(categoriesTestData.getCategories().get(0)));

		Car car2 = new Car();
		car2.setId("123456789B");
		car2.setManufacturer(manufacturerTestData.getManufacturers().get(1));
		car2.setModel("CX-5");
		car2.setYear(2022);
		car2.setCategories(Arrays.asList(categoriesTestData.getCategories().get(0)));

		Car car3 = new Car();
		car3.setId("123456789C");
		car3.setManufacturer(manufacturerTestData.getManufacturers().get(2));
		car3.setModel("Jetta");
		car3.setYear(2024);
		car3.setCategories(Arrays.asList(categoriesTestData.getCategories().get(1)));

		Car car4 = new Car();
		car4.setId("123456789D");
		car4.setManufacturer(manufacturerTestData.getManufacturers().get(0));
		car4.setModel("A6");
		car4.setYear(2024);
		car4.setCategories(Arrays.asList(categoriesTestData.getCategories().get(1)));

		cars = Arrays.asList(car1, car2, car3, car4);
	}
}
