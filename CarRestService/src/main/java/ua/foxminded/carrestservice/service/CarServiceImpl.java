package ua.foxminded.carrestservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.exception.car.CarAlreadyExistsException;
import ua.foxminded.carrestservice.exception.car.CarNotFoundException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerNotFoundException;
import ua.foxminded.carrestservice.mapper.CarMapper;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarMappingContext;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.repository.CarRepository;
import ua.foxminded.carrestservice.repository.CarSpecification;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

	private final CarRepository carRepository;
	private final ManufacturerService manufacturerService;
	private final CategoriesService categoriesService;
	private final GenerateCarIdService generateCarIdService;
	private final CarMapper carMapper;

	@PersistenceContext
	private EntityManager entityManager;

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
	public Optional<Car> findOneCar(CarSearchCriteria criteria) {
	    Specification<Car> spec = Specification.where(
	            CarSpecification.hasManufacturer(criteria.getManufacturer()))
	            .and(CarSpecification.hasModel(criteria.getModel()))
	            .and(CarSpecification.hasYear(criteria.getMinYear(), criteria.getMaxYear()))
	            .and(CarSpecification.hasCategories(criteria.getCategories()));

	    return carRepository.findOne(spec);
	}

	@Override
	@Transactional(readOnly = true)
    public Page<Car> findAllCarsByCriteria(CarSearchCriteria criteria, Pageable pageable) {
		Specification<Car> spec = Specification.where(
	            CarSpecification.hasManufacturer(criteria.getManufacturer()))
	            .and(CarSpecification.hasModel(criteria.getModel()))
	            .and(CarSpecification.hasYear(criteria.getMinYear(), criteria.getMaxYear()))
	            .and(CarSpecification.hasCategories(criteria.getCategories()));

        return carRepository.findAll(spec, pageable);
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
	public List<Car> createCars(List<Car> cars) {
		if (cars == null || cars.isEmpty()) {
			log.error("The car list must not be null or empty");
			throw new IllegalArgumentException("The car list must not be null or empty");
		}

		return carRepository.saveAll(cars);
	}

	@Override
	@Transactional
	public Car createCar(String manufacturerName, String model, Integer year, List<String> categories) {
		CarSearchCriteria criteria = CarSearchCriteria.builder()
                .manufacturer(manufacturerName)
                .model(model)
                .minYear(year)
                .maxYear(year)
                .build();

		Optional<Car> existingCar = findOneCar(criteria);
		if (existingCar.isPresent()) {
			log.info("Car already exist: " + existingCar.get());
			throw new CarAlreadyExistsException("Car already exist: " + existingCar.get()) ;
		}

		Car car = new Car();
		car.setManufacturer(manufacturerService.findManufacturerByName(manufacturerName)
				 .orElseGet(() -> manufacturerService.create(manufacturerName)));
		car.setModel(model);
		car.setYear(year);
		car.setCategories(categoriesService.findByName(categories));

		return createCar(car);
	}

    @Override
    @Transactional
	public Car updateCar(CarDto existingCarDto, CarDto updatedCar) {

    	CarSearchCriteria criteria = CarSearchCriteria.builder()
                .manufacturer(existingCarDto.manufacturer())
                .model(existingCarDto.model())
                .minYear(existingCarDto.year())
                .maxYear(existingCarDto.year())
                .build();

    	CarDto existingCar = carMapper.toDto(findOneCar(criteria)
	            .orElseThrow(() -> new CarNotFoundException("Car not found")));

		String updatedManufacturer = (updatedCar.manufacturer() != null) ?
				updatedCar.manufacturer(): existingCar.manufacturer();
		String updatedModel = (updatedCar.model() != null) ?
				updatedCar.model() : existingCar.model();
		Integer updatedYear = (updatedCar.year() != null) ?
				updatedCar.year() : existingCar.year();
		List<String> updatedCategories = (updatedCar.categories() != null && !updatedCar.categories().isEmpty()) ?
				updatedCar.categories() : existingCar.categories();

		CarMappingContext context = getCarContext(updatedCar);

		CarDto carToUpdate = new CarDto(
				existingCar.id(),
				updatedManufacturer,
				updatedModel,
				updatedYear,
				updatedCategories);

		return carRepository.save(carMapper.toModel(carToUpdate, context));
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

	private CarMappingContext getCarContext (CarDto carDto) {
		return new CarMappingContext(
				 manufacturerService.findManufacturerByName(carDto.manufacturer())
				 	.orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer with name: "
				 			+ carDto.manufacturer() + " does not exist")),
				 categoriesService.findByName(carDto.categories())
		    );
	}
}
