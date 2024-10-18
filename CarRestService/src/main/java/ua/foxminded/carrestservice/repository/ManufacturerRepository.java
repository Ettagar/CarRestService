package ua.foxminded.carrestservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.foxminded.carrestservice.model.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long>{

	Optional<Manufacturer> findByName(String name);
}
