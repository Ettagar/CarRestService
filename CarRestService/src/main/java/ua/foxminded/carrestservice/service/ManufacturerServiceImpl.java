package ua.foxminded.carrestservice.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerAlreadyExistsException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerInvalidException;
import ua.foxminded.carrestservice.exception.manufacturer.ManufacturerNotFoundException;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.repository.ManufacturerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {
	private final ManufacturerRepository manufacturerRepository;

	@Override
	@Transactional
	public Manufacturer create(String manufacturerName) {
		validateManufacturerName(manufacturerName);

		Optional<Manufacturer> existingManufacturer = findManufacturerByName(manufacturerName);

		if (existingManufacturer.isPresent()) {
			throw new ManufacturerAlreadyExistsException(
					"Manufacturer already exists with name: " + manufacturerName);
		}

		Manufacturer newManufacturer = new Manufacturer();
		newManufacturer.setName(manufacturerName);

		return manufacturerRepository.save(newManufacturer);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Manufacturer> findAll(Pageable pageable) {

		return manufacturerRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Manufacturer> findManufacturerByName(String manufacturerName) {
		validateManufacturerName(manufacturerName);

		return manufacturerRepository.findByName(manufacturerName);
	}

	@Override
	@Transactional
	public Manufacturer updateName(String manufacturerName, String manufacturerNewName) {

		Manufacturer manufacturer = manufacturerRepository.findByName(manufacturerName)
	            .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer does not exists with name: " + manufacturerName));

		validateManufacturerName(manufacturerNewName);
		manufacturer.setName(manufacturerNewName);

		return manufacturerRepository.save(manufacturer);
	}

	@Override
	public void delete(String manufacturerName) {
		validateManufacturerName(manufacturerName);

		Manufacturer manufacturer = manufacturerRepository.findByName(manufacturerName)
	            .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer does not exists with name: " + manufacturerName));

	    manufacturerRepository.delete(manufacturer);

	}

	 private void validateManufacturerName(String manufacturerName) {
	        if (manufacturerName == null || manufacturerName.trim().isEmpty()) {
	            throw new ManufacturerInvalidException("Manufacturer name cannot be empty.");
	        }

	        if (manufacturerName.length() > 40) {
	            throw new ManufacturerInvalidException("Manufacturer name cannot be longer than 40 characters.");
	        }
	    }
}
