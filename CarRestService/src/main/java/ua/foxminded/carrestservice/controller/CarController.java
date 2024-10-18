package ua.foxminded.carrestservice.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ua.foxminded.carrestservice.mapper.CarMapper;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.service.CarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarController {
	private final CarService carService;
	private final CarMapper carMapper;

	@GetMapping
	public Page<CarDto> searchCars(
	        @RequestParam(required = false) String manufacturer,
	        @RequestParam(required = false) String model,
	        @RequestParam(required = false) Integer minYear,
	        @RequestParam(required = false) Integer maxYear,
	        @RequestParam(required = false) List<String> categories,
	        @RequestParam(required = false, defaultValue = "model") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
	        Pageable pageable) {

		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		CarSearchCriteria criteria = CarSearchCriteria.builder()
		        .manufacturer(manufacturer)
		        .model(model)
		        .minYear(minYear)
		        .maxYear(maxYear)
		        .categories(categories)
		        .build();

        Page<Car> carPage = carService.findAllCarsByCriteria(criteria, sortedPageable);
        List<CarDto> carDtos = carMapper.toDto(carPage.getContent());

        return new PageImpl<>(carDtos, sortedPageable, carPage.getTotalElements());
	}
}
