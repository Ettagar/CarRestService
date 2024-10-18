package ua.foxminded.carrestservice.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ua.foxminded.carrestservice.model.Manufacturer;

public interface ManufacturerService {

	Manufacturer create(String manufacturerName);

	Page<Manufacturer> findAll(Pageable pageable);

	Optional<Manufacturer> findManufacturerByName(String manufacturerName);

	Manufacturer updateName(String manufacturerName, String manufacturerNewName);

	void delete(String manufacturerName);


}
