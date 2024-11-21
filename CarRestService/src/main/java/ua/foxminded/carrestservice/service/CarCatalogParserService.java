package ua.foxminded.carrestservice.service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.Category;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarCatalogParserService {

	@Value("${file.path.carscatalog}")
	private String fileName;

	private final ManufacturerServiceImpl manufacturerService;
	private final CategoryService categoryService;
	private final CarServiceImpl carServiceImpl;

	@Transactional
	public void parseDataFromCsv() {
		List<Car> cars = new ArrayList<>();
		log.info("Filename = " + fileName);
		try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {
			List<String[]> records = reader.readAll();
			for (int i = 1; i < records.size() - 1; i++) {
				String[] recordLine = records.get(i);
				Car car = new Car();
				car.setId(recordLine[0]);
				manufacturerService.create(recordLine[1]);
				car.setManufacturer(manufacturerService.findManufacturerByName(recordLine[1]).get());
				car.setYear(Integer.parseInt(recordLine[2]));
				car.setModel(recordLine[3]);

				String[] categoriesArray = recordLine[4].split(",\\s*");
				List<Category> categories = new ArrayList<>();
				for (String categoryName : categoriesArray) {
					categoryName = categoryName.replaceAll("\\d", "");
					Optional<Category> category = categoryService.findByName(categoryName);
					if (category.isEmpty()) {
						category = Optional.of(categoryService.createCategory(categoryName));
					}
					categories.add(category.get());
				}
				car.setCategories(categories);

				cars.add(car);
				log.info("Parsed: {}", car);
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
		carServiceImpl.createCars(cars);
	}
}
