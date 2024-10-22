package ua.foxminded.carrestservice.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;

public interface ManufacturerService {

	ManufacturerDto create(String manufacturerName);

	Page<ManufacturerDto> findAll(Pageable pageable);

	Optional<Manufacturer> findManufacturerByName(String manufacturerName);

	ManufacturerDto updateName(String manufacturerName, String manufacturerNewName);

	void delete(String manufacturerName);


}
