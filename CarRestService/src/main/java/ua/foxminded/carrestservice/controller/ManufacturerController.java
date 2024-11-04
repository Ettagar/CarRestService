package ua.foxminded.carrestservice.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerAlreadyExistsException;
import ua.foxminded.carrestservice.model.CarSearchCriteria;
import ua.foxminded.carrestservice.model.dto.CarDto;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;
import ua.foxminded.carrestservice.service.CarService;
import ua.foxminded.carrestservice.service.ManufacturerService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manufacturers")
public class ManufacturerController {
	private final ManufacturerService manufacturerService;
	private final CarService carService;

	@GetMapping
	public ResponseEntity<Page<ManufacturerDto>> getAllManufacturers(
			@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		return ResponseEntity.ok(manufacturerService.findAll(pageable));
	}

	@PostMapping
	public ResponseEntity<ManufacturerDto> createManufacturer(
			@RequestParam(required = true)  String manufacturerName) {

		if (manufacturerService.findManufacturerByName(manufacturerName).isPresent()) {
			throw new ManufacturerAlreadyExistsException("Manufacturer '" + manufacturerName + "' already exist");
		}
		ManufacturerDto manufacturer = manufacturerService.create(manufacturerName);
		log.info("Succesfully created manufacturer with name: %S", manufacturerName);

		return ResponseEntity.status(HttpStatus.CREATED).body(manufacturer);
	}

	@PutMapping("/{manufacturerName}")
	public ResponseEntity<ManufacturerDto> updateManufacturer(
			@PathVariable String manufacturerName,
			@RequestParam(required = true) String manufacturerNewName) {

		ManufacturerDto updatedManufacturer = manufacturerService.updateName(manufacturerName, manufacturerNewName);
		log.info("Succesfully updated manufacturer %S with name: %S", manufacturerName, manufacturerNewName);

		return ResponseEntity.ok(updatedManufacturer);
	}

	@DeleteMapping("/{manufacturerName}")
	public ResponseEntity<String> deleteManufacturer(
			@PathVariable String manufacturerName) {

		manufacturerService.delete(manufacturerName);
		log.info("Manufacturer %S deleted successfully", manufacturerName);

		return ResponseEntity.ok("Manufacturer '" + manufacturerName + "' deleted successfully");
	}

	@GetMapping("/{manufacturer}/models")
	public ResponseEntity<Page<CarDto>> getAllCarsByManufacturer(
			@PathVariable String manufacturer,
			@PageableDefault(sort = "model", direction = Sort.Direction.ASC) Pageable pageable) {

		CarSearchCriteria criteria = CarSearchCriteria.builder()
				.manufacturer(manufacturer)
				.build();

		Page<CarDto> carPage = carService.findAllCarsByCriteria(criteria, pageable);

		return ResponseEntity.ok(carPage);
	}

	@PostMapping("/{manufacturer}/models")
	public ResponseEntity<CarDto> createCarForManufacturer(
			@PathVariable String manufacturer,
			@RequestParam(required = true) String model,
			@RequestParam(required = true) Integer year,
			@RequestParam(required = true) List<String> categories) {

		CarDto createdCar = carService.createCar(manufacturer, model, year, categories);

		return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
	}

	@GetMapping("/{manufacturer}/models/{model}")
	public ResponseEntity<Page<CarDto>> getAllCarsByManufacturerAndModel(
			@PathVariable String manufacturer,
			@PathVariable String model,
			@PageableDefault(sort = "year", direction = Sort.Direction.ASC) Pageable pageable) {

		CarSearchCriteria criteria = CarSearchCriteria.builder()
				.manufacturer(manufacturer)
				.model(model)
				.build();

		Page<CarDto> carPage = carService.findAllCarsByCriteria(criteria, pageable);

		return ResponseEntity.ok(carPage);
	}

	@GetMapping("/{manufacturer}/models/{model}/{year}")
	public ResponseEntity<Page<CarDto>> getCarByManufacturerAndModelAndYear(
			@PathVariable String manufacturer,
			@PathVariable String model,
			@PathVariable Integer year,
			@PageableDefault(sort = "year", direction = Sort.Direction.ASC) Pageable pageable) {

		CarSearchCriteria criteria = CarSearchCriteria.builder()
				.manufacturer(manufacturer)
				.model(model)
				.minYear(year)
				.maxYear(year)
				.build();

		Page<CarDto> carPage = carService.findAllCarsByCriteria(criteria, pageable);

		return ResponseEntity.ok(carPage);
	}

	@PostMapping("/{manufacturer}/models/{model}/{year}")
	public ResponseEntity<CarDto> createCarForManufacturerWithModelAndYear(
			@PathVariable String manufacturer,
			@PathVariable String model,
			@PathVariable Integer year,
			@RequestParam List<String> categories
			) {

		CarDto newCar = carService.createCar(manufacturer, model, year, categories);

		return ResponseEntity.status(HttpStatus.CREATED).body(newCar);
	}

	@PatchMapping("/{manufacturer}/models/{model}/{year}")
	public ResponseEntity<CarDto> patchCarForManufacturerWithModelAndYear(
			@PathVariable String manufacturer,
			@PathVariable String model,
			@PathVariable Integer year,
			@RequestParam(required = false) String newManufacturer,
			@RequestParam(required = false) String newModel,
			@RequestParam(required = false) Integer newYear,
			@RequestParam(required = false) List<String> newCategories
			) {

		CarDto existingCarDto = new CarDto (manufacturer, model, year, Collections.emptyList());
		log.info("Controller existingCarDto:" + existingCarDto);
		CarDto updatedCarDto = new CarDto (newManufacturer, newModel, newYear, newCategories);
		log.info("Controller updatedCarDto:" + updatedCarDto);
		CarDto patchedCar = carService.updateCar(existingCarDto, updatedCarDto);
		log.info("Controller patchedCar:" + patchedCar);

		return ResponseEntity.ok(patchedCar);
	}


	@DeleteMapping("/{manufacturer}/models/{model}/{year}")
	public ResponseEntity<Void> deleteCar(
			@PathVariable String manufacturer,
			@PathVariable String model,
			@PathVariable int year) {

		carService.deleteCars(manufacturer, model, year);

		return ResponseEntity.noContent().build();
	}
}
