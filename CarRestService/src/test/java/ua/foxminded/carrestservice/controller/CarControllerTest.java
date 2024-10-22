package ua.foxminded.carrestservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.foxminded.carrestservice.mapper.CarMapper;
import ua.foxminded.carrestservice.mapper.CarMapperImpl;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.service.CarService;
import ua.foxminded.carrestservice.util.ApplicationTestData;

@WebMvcTest(controllers = CarController.class)
@Import({ApplicationTestData.class, CarMapperImpl.class})
class CarControllerTest {
	private static final String END_POINT_PATH = ("/api/v1/cars");
	private static final Integer PAGE_NUMBER = 0;
	private static final Integer PAGE_SIZE = 20;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApplicationTestData testData;

	@Autowired
	private CarMapper carMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CarService carService;

	@BeforeEach
	public void setUp() {
		testData.setUp();
	}

	@Test
	void testSearchAllCars() throws Exception {
		Sort sort = Sort.by(Sort.Direction.ASC, "model");
		Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort);
		List<CarDto> carDtos = carMapper.toDtoList(testData.getCars());

		when(carService.findAllCarsByCriteria(any(CarSearchCriteria.class), eq(pageable)))
				.thenReturn(new PageImpl<>(carDtos, pageable, carDtos.size()));


		Page<CarDto> expectedPage = new PageImpl<>(carDtos, pageable, carDtos.size());
		String expectedJson = objectMapper.writeValueAsString(expectedPage);

		mockMvc.perform(get(END_POINT_PATH)
				.param("page", String.valueOf(PAGE_NUMBER))
				.param("size", String.valueOf(PAGE_SIZE))
				.param("sortBy", "model")
				.param("sortDirection", "ASC"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(content().json(expectedJson));
	}

	@Test
	void testSearchCarsByCriteria() throws Exception {
		final String manufacturer = "Audi";
		final Integer maxYear = 2021;

		Sort sort = Sort.by(Sort.Direction.ASC, "model");
		Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, sort);

		List<CarDto> cars = carMapper.toDtoList(
				testData.getCars().stream()
					.filter(car -> car.getManufacturer().getName().equals(manufacturer)
								&& car.getYear() <= maxYear)
					.collect(Collectors.toList())
		);
		Page<CarDto> expectedPage = new PageImpl<>(cars, pageable, cars.size());

		when(carService.findAllCarsByCriteria(any(CarSearchCriteria.class), eq(pageable)))
				.thenReturn(expectedPage);
		String expectedJson = objectMapper.writeValueAsString(expectedPage);

		mockMvc.perform(get(END_POINT_PATH)
				.param("page", PAGE_NUMBER.toString())
				.param("size", PAGE_SIZE.toString())
				.param("sortBy", "model")
				.param("sortDirection", "ASC")
				.param("manufacturer", manufacturer)
				.param("maxYear", String.valueOf(maxYear)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(content().json(expectedJson));
	}
}
