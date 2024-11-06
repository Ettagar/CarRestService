package ua.foxminded.carrestservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerInvalidException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerNotFoundException;
import ua.foxminded.carrestservice.mapper.ManufacturerMapper;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;
import ua.foxminded.carrestservice.repository.ManufacturerRepository;
import ua.foxminded.carrestservice.util.ApplicationTestData;

class ManufacturerServiceImplTest {

	@Mock
	private ManufacturerRepository manufacturerRepository;

	@Mock
	private ManufacturerMapper manufacturerMapper;

	@InjectMocks
	private ManufacturerServiceImpl manufacturerService;

	@Autowired
	private ApplicationTestData testData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		testData = new ApplicationTestData();
		testData.setUp();
	}

	@Test
	void testCreate_shouldReturnExistingManufacturer_whenManufacturerAlreadyExists() {
		Manufacturer existingManufacturer = testData.getManufacturers().get(0);
		ManufacturerDto existingManufacturerDto = testData.getManufacturerTestData().getManufacturersDtos().get(0);

		when(manufacturerRepository.findByName(existingManufacturer.getName()))
		.thenReturn(Optional.of(existingManufacturer));
		when(manufacturerMapper.toDto(existingManufacturer)).thenReturn(existingManufacturerDto);

		ManufacturerDto result = manufacturerService.create(existingManufacturer.getName());

		assertEquals(existingManufacturerDto, result);
		verify(manufacturerRepository, never()).save(any());
	}

	@Test
	void testCreate_shouldCreateAndReturnNewManufacturer_whenManufacturerDoesNotExist() {
		Manufacturer newManufacturer = testData.getManufacturers().get(1);
		ManufacturerDto newManufacturerDto = testData.getManufacturerTestData().getManufacturersDtos().get(1);

		when(manufacturerRepository.findByName(newManufacturer.getName())).thenReturn(Optional.empty());
		when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(newManufacturer);
		when(manufacturerMapper.toDto(newManufacturer)).thenReturn(newManufacturerDto);

		ManufacturerDto result = manufacturerService.create(newManufacturer.getName());

		assertEquals(newManufacturerDto, result);
		verify(manufacturerRepository).save(any(Manufacturer.class));
	}

	@Test
	void testCreate_shouldThrowException_whenManufacturerNameIsInvalid() {
		String invalidName = "   ";
		assertThrows(ManufacturerInvalidException.class, () -> manufacturerService.create(invalidName));
	}

	@Test
	void testFindAll_shouldReturnPagedManufacturers() {
		Pageable pageable = PageRequest.of(0, 10);
		Manufacturer manufacturer = testData.getManufacturers().get(2);
		ManufacturerDto manufacturerDto = testData.getManufacturerTestData().getManufacturersDtos().get(2);
		Page<Manufacturer> manufacturerPage = new PageImpl<>(List.of(manufacturer));

		when(manufacturerRepository.findAll(pageable)).thenReturn(manufacturerPage);
		when(manufacturerMapper.toDto(manufacturer)).thenReturn(manufacturerDto);

		Page<ManufacturerDto> result = manufacturerService.findAll(pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(manufacturerDto, result.getContent().get(0));
	}

	@Test
	void testFindManufacturerByName_shouldReturnManufacturer_whenManufacturerExists() {
		Manufacturer manufacturer = testData.getManufacturers().get(0);

		when(manufacturerRepository.findByName(manufacturer.getName())).thenReturn(Optional.of(manufacturer));

		Optional<Manufacturer> result = manufacturerService.findManufacturerByName(manufacturer.getName());

		assertTrue(result.isPresent());
		assertEquals(manufacturer, result.get());
	}

	@Test
	void testFindManufacturerByName_shouldThrowException_whenManufacturerNameIsInvalid() {
		String invalidName = null;
		assertThrows(ManufacturerInvalidException.class, () -> manufacturerService.findManufacturerByName(invalidName));
	}

	@Test
	void testUpdateName_shouldUpdateManufacturerName_whenManufacturerExists() {
		Manufacturer manufacturer = testData.getManufacturers().get(0);
		ManufacturerDto updatedManufacturerDto = new ManufacturerDto(manufacturer.getId(), "Updated Name");

		when(manufacturerRepository.findByName(manufacturer.getName())).thenReturn(Optional.of(manufacturer));
		when(manufacturerRepository.save(any(Manufacturer.class))).thenReturn(manufacturer);
		when(manufacturerMapper.toDto(manufacturer)).thenReturn(updatedManufacturerDto);

		ManufacturerDto result = manufacturerService.updateName(manufacturer.getName(), "Updated Name");

		assertEquals(updatedManufacturerDto, result);
		verify(manufacturerRepository).save(manufacturer);
	}

	@Test
	void testUpdateName_shouldThrowException_whenManufacturerDoesNotExist() {
		String oldName = "NonExistent";
		String newName = "UpdatedName";

		when(manufacturerRepository.findByName(oldName)).thenReturn(Optional.empty());

		assertThrows(ManufacturerNotFoundException.class, () -> manufacturerService.updateName(oldName, newName));
	}

	@Test
	void testUpdateName_shouldThrowException_whenNewManufacturerNameIsInvalid() {
		Manufacturer manufacturer = testData.getManufacturers().get(0);
		String invalidNewName = "  ";

		when(manufacturerRepository.findByName(manufacturer.getName())).thenReturn(Optional.of(manufacturer));

		assertThrows(ManufacturerInvalidException.class,
				() -> manufacturerService.updateName(manufacturer.getName(), invalidNewName));
	}

	@Test
	void testDelete_shouldDeleteManufacturer_whenManufacturerExists() {
		Manufacturer manufacturer = testData.getManufacturers().get(1);

		when(manufacturerRepository.findByName(manufacturer.getName())).thenReturn(Optional.of(manufacturer));

		manufacturerService.delete(manufacturer.getName());

		verify(manufacturerRepository).delete(manufacturer);
	}

	@Test
	void testDelete_shouldThrowException_whenManufacturerDoesNotExist() {
		String manufacturerName = "Unknown";

		when(manufacturerRepository.findByName(manufacturerName)).thenReturn(Optional.empty());

		assertThrows(ManufacturerNotFoundException.class, () -> manufacturerService.delete(manufacturerName));
	}
}
