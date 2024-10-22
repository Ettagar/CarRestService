package ua.foxminded.carrestservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.exception.car.CarAlreadyExistsException;
import ua.foxminded.carrestservice.exception.car.CarNotFoundException;
import ua.foxminded.carrestservice.mapper.CarMapper;
import ua.foxminded.carrestservice.mapper.PageMapper;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.repository.CarRepository;
import ua.foxminded.carrestservice.repository.CarSpecification;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

	private final CarRepository carRepository;
	private final ManufacturerService manufacturerService;
	private final CategoryService categoryService;
	private final GenerateCarIdService generateCarIdService;
	private final CarMapper carMapper;

	@Override
	@Transactional(readOnly = true)
	public Optional<Car> findById(String id) {
		if (id == null) {
			log.error("The carId must not be null");
            throw new IllegalArgumentException("The carId must not be null");
		}

		return carRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CarDto> findOneCar(CarSearchCriteria criteria) {
	    Specification<Car> spec = Specification.where(
	            CarSpecification.hasManufacturer(criteria.getManufacturer()))
	            .and(CarSpecification.hasModel(criteria.getModel()))
	            .and(CarSpecification.hasYear(criteria.getMinYear(), criteria.getMaxYear()))
	            .and(CarSpecification.hasCategories(criteria.getCategories()));

	    Optional<Car> carOptional = carRepository.findOne(spec);

	    return carOptional.map(carMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
    public Page<CarDto> findAllCarsByCriteria(CarSearchCriteria criteria, Pageable pageable) {
		Specification<Car> spec = Specification.where(
				CarSpecification.hasManufacturer(criteria.getManufacturer()))
				.and(CarSpecification.hasModel(criteria.getModel()))
				.and(CarSpecification.hasYear(criteria.getMinYear(), criteria.getMaxYear()))
				.and(CarSpecification.hasCategories(criteria.getCategories()));

		return PageMapper.mapEntityPageToDtoPage(
				carRepository.findAll(spec, pageable),
				carMapper::toDto);
	}

	@Override
	@Transactional
	public Car createCar(Car car) {
		if (car == null || car.getModel().isEmpty()) {
			log.error("The car model must not be null or empty");
			throw new IllegalArgumentException("The car model must not be null or empty");
		}
		if (car.getId() == null || car.getId().isEmpty()) {
			String generatedId = generateCarIdService.generateUniquetId();
			car.setId(generatedId);
		}

		return carRepository.save(car);
	}

	@Override
	@Transactional
	public List<CarDto> createCars(List<Car> cars) {
		if (cars == null || cars.isEmpty()) {
			log.error("The car list must not be null or empty");
			throw new IllegalArgumentException("The car list must not be null or empty");
		}

		return carMapper.toDtoList(carRepository.saveAll(cars));
	}

	@Override
	@Transactional
	public CarDto createCar(String manufacturerName, String model, Integer year, List<String> categories) {
		CarSearchCriteria criteria = CarSearchCriteria.builder()
                .manufacturer(manufacturerName)
                .model(model)
                .minYear(year)
                .maxYear(year)
                .build();

		Optional<CarDto> existingCar = findOneCar(criteria);
		if (existingCar.isPresent()) {
			log.info("Car already exist: " + existingCar.get());
			throw new CarAlreadyExistsException("Car already exist: " + existingCar.get()) ;
		}

		Car car = new Car();
		try {
			manufacturerService.create(manufacturerName);
		}
		finally {
			car.setManufacturer(
					manufacturerService.findManufacturerByName(manufacturerName).get());
		}
		car.setModel(model);
		car.setYear(year);
		car.setCategories(categoryService.findByName(categories));

		return carMapper.toDto(createCar(car));
	}

    @Override
    @Transactional
	public CarDto updateCar(CarDto existingCarContext, CarDto updatedCarContext) {

    	CarSearchCriteria criteria = CarSearchCriteria.builder()
                .manufacturer(existingCarContext.manufacturer())
                .model(existingCarContext.model())
                .minYear(existingCarContext.year())
                .maxYear(existingCarContext.year())
                .build();

    	CarDto existingCar = findOneCar(criteria)
	            .orElseThrow(() -> new CarNotFoundException("Car not found"));

		String updatedManufacturerDto = (updatedCarContext.manufacturer() != null) ?
				updatedCarContext.manufacturer(): existingCar.manufacturer();
		String updatedModel = (updatedCarContext.model() != null) ?
				updatedCarContext.model() : existingCar.model();
		Integer updatedYear = (updatedCarContext.year() != null) ?
				updatedCarContext.year() : existingCar.year();
		List<String> updatedCategoriesDto = (updatedCarContext.categories() != null
				&& !updatedCarContext.categories().isEmpty()) ?
				updatedCarContext.categories() : existingCar.categories();

		manufacturerService.create(updatedManufacturerDto);
		Manufacturer updatedManufacturer = manufacturerService.findManufacturerByName(updatedManufacturerDto).get();
		List<Category> updatedCategories = categoryService.findByName(updatedCategoriesDto);

		Car carToUpdate = Car.builder()
				.id(existingCar.id())
				.manufacturer(updatedManufacturer)
				.model(updatedModel)
				.year(updatedYear)
				.categories(updatedCategories)
				.build();

		CarDto updatedCarDto = carMapper.toDto(carRepository.save(carToUpdate));
		log.info("Service updatedCarDTO" + updatedCarDto);

		return updatedCarDto;
	}

	@Override
    @Transactional
    public void deleteCars(String manufacturer, String model, Integer year) {
        Specification<Car> spec = Specification.where(
        		CarSpecification.hasManufacturer(manufacturer))
                .and(CarSpecification.hasModel(model))
                .and(CarSpecification.hasYear(year, year));

        List<Car> carsToDelete = carRepository.findAll(spec);

        if (!carsToDelete.isEmpty()) {
            carRepository.deleteAll(carsToDelete);
        }
    }
}
