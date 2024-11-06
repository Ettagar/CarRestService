package ua.foxminded.carrestservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import ua.foxminded.carrestservice.exception.car.CarAlreadyExistsException;
import ua.foxminded.carrestservice.mapper.CarMapper;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.repository.CarRepository;
import ua.foxminded.carrestservice.util.ApplicationTestData;

@Import({ ApplicationTestData.class})
class CarServiceImplTest {

	@Mock
	private CarRepository carRepository;

	@Mock
	private ManufacturerService manufacturerService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private GenerateCarIdService generateCarIdService;

	@Mock
	private CarMapper carMapper;

	@Autowired
	private ApplicationTestData testData;

	@InjectMocks
	private CarServiceImpl carService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		testData = new ApplicationTestData();
		testData.setUp();
		Mockito.reset(carRepository, manufacturerService, categoryService);
	}

	@Test
	void testFindById_shouldReturnCar_whenCarExists() {
		Car car = testData.getCars().get(0);

		when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

		Optional<Car> result = carService.findById(car.getId());

		assertTrue(result.isPresent());
		assertEquals(car, result.get());
	}

	@Test
	void testFindAllCarsByCriteria_shouldReturnPagedCarDtos() {
		Car car = testData.getCars().get(0);
		CarDto carDto = testData.getCarDtos().get(0);
		Pageable pageable = Pageable.unpaged();
		Page<Car> carPage = new PageImpl<>(List.of(car));

		when(carRepository.findAll(ArgumentMatchers.<Specification<Car>>any(), eq(pageable))).thenReturn(carPage);
		when(carMapper.toDto(car)).thenReturn(carDto);

		CarSearchCriteria criteria = CarSearchCriteria.builder()
				.manufacturer(car.getManufacturer().getName())
				.model(car.getModel())
				.minYear(car.getYear())
				.maxYear(car.getYear())
				.categories(Arrays.asList("SUV"))
				.build();

		Page<CarDto> result = carService.findAllCarsByCriteria(criteria, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(carDto, result.getContent().get(0));
	}

	@Test
	void testCreateCar_shouldGenerateIdAndSaveCar_whenIdIsNull() {
		Car car = testData.getCars().get(0);
		car.setId(null);

		when(generateCarIdService.generateUniquetId()).thenReturn("GeneratedId");
		when(carRepository.save(car)).thenReturn(car);

		Car result = carService.createCar(car);

		assertEquals("GeneratedId", result.getId());
		verify(carRepository).save(car);
	}

	@Test
	void testCreateCar_shouldThrowException_whenCarModelIsEmpty() {
		Car car = new Car();
		car.setModel("");

		Exception exception = assertThrows(IllegalArgumentException.class, () -> carService.createCar(car));
		assertEquals("The car model must not be null or empty", exception.getMessage());
	}

	@Test
	void testCreateCars_shouldSaveAllCars_whenListIsNotEmpty() {
		List<Car> cars = testData.getCars();
		List<CarDto> carDtos = testData.getCarDtos();

		when(carRepository.saveAll(cars)).thenReturn(cars);
		when(carMapper.toDtoList(cars)).thenReturn(carDtos);

		List<CarDto> result = carService.createCars(cars);

		assertEquals(cars.size(), result.size());
		verify(carRepository).saveAll(cars);
	}

	@Test
	void testDeleteCars_shouldDeleteAllCarsMatchingCriteria() {
		Car car = testData.getCars().get(0);

		when(carRepository.findAll(ArgumentMatchers.<Specification<Car>>any())).thenReturn(List.of(car));

		carService.deleteCars(car.getManufacturer().getName(), car.getModel(), car.getYear());

		verify(carRepository).deleteAll(List.of(car));
	}

	@Test
	void testUpdateCar_shouldUpdateCar_whenValidCarDtoIsProvided() {
		CarDto carDto = testData.getCarDtos().get(0);
		CarDto carDtoUpdated = new CarDto(carDto.id(), carDto.manufacturer(), carDto.model(), carDto.year(),
				List.of("SUV", "Luxury"));
		Car car = testData.getCars().get(0);

		mockCarSetup(car, carDto);

		when(carRepository.save(ArgumentMatchers.<Car>any())).thenReturn(car);
		when(carMapper.toDto(car)).thenReturn(carDtoUpdated);

		CarDto result = carService.updateCar(carDto, carDtoUpdated);

		assertEquals(carDtoUpdated, result);
	}

	@Test
	void testCreateCar_shouldThrowCarAlreadyExistsException_whenCarAlreadyExists() {
		Car car = testData.getCars().get(0);
		CarDto carDto = testData.getCarDtos().get(0);

		mockCarSetup(car, carDto);

		assertThrows(CarAlreadyExistsException.class, () -> carService.createCar(carDto.manufacturer(),
				carDto.model(), carDto.year(), carDto.categories()));
	}

	private void mockCarSetup(Car car, CarDto carDto) {
		when(carRepository.findOne(ArgumentMatchers.<Specification<Car>>any())).thenReturn(Optional.of(car));
		when(manufacturerService.findManufacturerByName(carDto.manufacturer()))
		.thenReturn(Optional.of(car.getManufacturer()));
		when(categoryService.findByName(carDto.categories()))
		.thenReturn(car.getCategories());
		when(carMapper.toDto(car)).thenReturn(carDto);
	}

}
