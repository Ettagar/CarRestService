package ua.foxminded.carrestservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerAlreadyExistsException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerInvalidException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerNotFoundException;
import ua.foxminded.carrestservice.mapper.CarMapper;
import ua.foxminded.carrestservice.mapper.ManufacturerMapper;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;
import ua.foxminded.carrestservice.service.CarService;
import ua.foxminded.carrestservice.service.ManufacturerService;
import ua.foxminded.carrestservice.util.ApplicationTestData;

@ActiveProfiles("test")
@WebMvcTest(ManufacturerController.class)
@Import({ ApplicationTestData.class, ManufacturerMapper.class, CarMapper.class })
class ManufacturerControllerTest {
	private static final String END_POINT_PATH = ("/api/v1/manufacturers");
	private static final Integer PAGE_NUMBER = 0;
	private static final Integer PAGE_SIZE = 20;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApplicationTestData testData;

	@Autowired
	private ManufacturerMapper manufacturerMapper;

	@Autowired
	private CarMapper carMapper;

	@MockBean
	private ManufacturerService manufacturerService;

	@MockBean
	private CarService carService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		testData.setUp();
	}

	@Test
	void testGetAllManufacturers() throws Exception {
		Sort sort = Sort.by(Sort.Direction.ASC, "name");
		Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort);

		List<ManufacturerDto> manufacturerDtos = testData.getManufacturers().stream()
				.map(manufacturerMapper::toDto)
				.toList();
		Page<ManufacturerDto> expectedPage = new PageImpl<>(manufacturerDtos, pageable, manufacturerDtos.size());
		String expectedJson = objectMapper.writeValueAsString(expectedPage);

		when(manufacturerService.findAll(any(Pageable.class)))
			.thenReturn(new PageImpl<>(testData.getManufacturers(), pageable, testData.getManufacturers().size()));

		mockMvc.perform(get(END_POINT_PATH)
				.param("page", String.valueOf(PAGE_NUMBER))
				.param("size", String.valueOf(PAGE_SIZE)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateManufacturer() throws Exception {
		String manufacturerName = "Seat";

		Manufacturer createdManufacturerEntity = new Manufacturer(1L, manufacturerName, List.of());
		ManufacturerDto createdManufacturerDto = manufacturerMapper.toDto(createdManufacturerEntity);
		String expectedJson = objectMapper.writeValueAsString(createdManufacturerDto);

		when(manufacturerService.create(manufacturerName))
				.thenReturn(createdManufacturerEntity);

		mockMvc.perform(post(END_POINT_PATH)
				.param("manufacturerName", manufacturerName))
				.andExpect(status().isCreated())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateManufacturerIfAlreadyExist() throws Exception {
		String manufacturerName = "Audi";

		when(manufacturerService.create(manufacturerName))
				.thenThrow(	new ManufacturerAlreadyExistsException
						("Manufacturer already exists with name: " + manufacturerName));

		mockMvc.perform(post(END_POINT_PATH)
				.param("manufacturerName", manufacturerName))
				.andExpect(status().isConflict());
	}

	@Test
	void testCreateManufacturerIfEmpty() throws Exception {
		when(manufacturerService.create(""))
				.thenThrow(new ManufacturerInvalidException("Manufacturer name is invalid"));

		mockMvc.perform(post(END_POINT_PATH)
				.param("manufacturerName", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testUpdateManufacturerName() throws Exception {
		String manufacturerName = "Audi";
		String manufacturerNewName = "Oudi";

		Manufacturer updatedManufacturerEntity = new Manufacturer(1L, manufacturerNewName, List.of());
		ManufacturerDto updatedManufacturerDto = manufacturerMapper.toDto(updatedManufacturerEntity);
		String expectedJson = objectMapper.writeValueAsString(updatedManufacturerDto);

		when(manufacturerService.updateName(manufacturerName, manufacturerNewName))
				.thenReturn(updatedManufacturerEntity);

		mockMvc.perform(put(END_POINT_PATH + "/{manufacturerName}", manufacturerName)
				.param("manufacturerNewName", manufacturerNewName))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testUpdateManufacturerNameToEmpty() throws Exception {
		String manufacturerName = "Audi";
		String manufacturerNewName = "";

		when(manufacturerService.updateName(manufacturerName, manufacturerNewName))
				.thenThrow(new ManufacturerInvalidException("Manufacturer name is invalid"));

		mockMvc.perform(put(END_POINT_PATH + "/{manufacturerName}", manufacturerName)
				.param("manufacturerNewName", manufacturerNewName))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testDeleteManufacturer() throws Exception {
		String manufacturerName = "Audi";

		doNothing().when(manufacturerService).delete(manufacturerName);

		mockMvc.perform(delete(END_POINT_PATH + "/{manufacturerName}", manufacturerName))
				.andExpect(status().isOk())
				.andExpect(content().string("Manufacturer '" + manufacturerName + "' deleted successfully"));
	}

	@Test
	void testDeleteNonExistentManufacturer() throws Exception {
		String manufacturerName = "NonExistent";

		doThrow(new ManufacturerNotFoundException("Manufacturer does not exist with name: " + manufacturerName))
				.when(manufacturerService).delete(manufacturerName);

		mockMvc.perform(delete(END_POINT_PATH + "/{manufacturerName}", manufacturerName))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetAllCarsByManufacturer() throws Exception {
		String manufacturerName = "Audi";

		Sort sort = Sort.by(Sort.Direction.ASC, "model");
		Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort);

		List<Car> cars = testData.getCars().stream()
				.filter(car -> car.getManufacturer().getName().equals(manufacturerName))
				.collect(Collectors.toList());
		List<CarDto> carDtos  = carMapper.toDto(cars);
		Page<CarDto> expectedPage = new PageImpl<>(carDtos, pageable, carDtos.size());
		String expectedJson = objectMapper.writeValueAsString(expectedPage);

		when(carService.findAllCarsByCriteria(any(CarSearchCriteria.class), eq(pageable)))
				.thenReturn(new PageImpl<>(cars, pageable, cars.size()));

		mockMvc.perform(get(END_POINT_PATH + "/" + manufacturerName + "/models")
				.param("page", PAGE_NUMBER.toString())
				.param("size",PAGE_SIZE.toString()))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateCarForManufacturer() throws Exception {
		Car createdCar = testData.getCars().get(0);
		CarDto expectedCarDto = carMapper.toDto(createdCar);

		when(carService.createCar(
				expectedCarDto.manufacturer(),
				expectedCarDto.model(),
				expectedCarDto.year(),
				expectedCarDto.categories()))
		.thenReturn(createdCar);

	    mockMvc.perform(post(END_POINT_PATH + "/" + expectedCarDto.manufacturer() + "/models")
                .param("model", expectedCarDto.model())
                .param("year", expectedCarDto.year().toString())
                .param("categories", expectedCarDto.categories().get(0)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedCarDto)));
	}

	@Test
	void testGetCarByManufacturerAndModelAndYear() throws Exception {
		Sort sort = Sort.by(Sort.Direction.ASC, "year");
		Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort);

		List<Car> cars = Arrays.asList(testData.getCars().get(0));
		List<CarDto> carDtos  = carMapper.toDto(cars);
		Page<CarDto> expectedPage = new PageImpl<>(carDtos, pageable, carDtos.size());
		String expectedJson = objectMapper.writeValueAsString(expectedPage);

		when(carService.findAllCarsByCriteria(any(CarSearchCriteria.class), eq(pageable)))
				.thenReturn(new PageImpl<>(cars, pageable, cars.size()));

		mockMvc.perform(get(END_POINT_PATH + "/" + carDtos.get(0).manufacturer()
				+ "/models/" + carDtos.get(0).model() + "/" + carDtos.get(0).year())
				.param("page", PAGE_NUMBER.toString())
				.param("size",PAGE_SIZE.toString()))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateCarForManufacturerWithModelAndYear() throws Exception {
		Car createdCar = testData.getCars().get(0);
		CarDto expectedCarDto = carMapper.toDto(createdCar);

		when(carService.createCar(
				expectedCarDto.manufacturer(),
				expectedCarDto.model(),
				expectedCarDto.year(),
				expectedCarDto.categories()))
		.thenReturn(createdCar);

	    mockMvc.perform(post(END_POINT_PATH + "/" + expectedCarDto.manufacturer()
	    	+ "/models/" + expectedCarDto.model() + "/" + expectedCarDto.year())
                .param("categories", expectedCarDto.categories().get(0)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedCarDto)));
	}

	@Test
	void testPatchCarManufacturerAndModelAndYearAndCategories() throws Exception {
		Car existingCar = testData.getCars().get(0);
		CarDto existingCarDto = new CarDto (
			"",
			existingCar.getManufacturer().getName(),
			existingCar.getModel(),
			existingCar.getYear(),
			Collections.emptyList()
		);

		Car updatedCar = testData.getCars().get(1);
		CarDto updatedCarDto = new CarDto (
				"",
				updatedCar.getManufacturer().getName(),
				updatedCar.getModel(),
				updatedCar.getYear(),
				updatedCar.getCategories().stream()
					.map(Category :: getName)
					.collect(Collectors.toList())
		);

		Car patchedCar = updatedCar;
		patchedCar.setId(existingCar.getId());
		CarDto patchedCarDto = carMapper.toDto(patchedCar);

		when(carService.updateCar(existingCarDto, updatedCarDto))
			.thenReturn(patchedCar);

	    mockMvc.perform(patch(END_POINT_PATH + "/" + existingCarDto.manufacturer()
	    	+ "/models/" + existingCarDto.model() + "/" + existingCarDto.year())
                .param("newManufacturer", updatedCarDto.manufacturer())
                .param("newModel", updatedCarDto.model())
                .param("newYear", updatedCarDto.year().toString())
                .param("newCategories", updatedCarDto.categories().toArray(new String[0])))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(patchedCarDto)));
	}
}
