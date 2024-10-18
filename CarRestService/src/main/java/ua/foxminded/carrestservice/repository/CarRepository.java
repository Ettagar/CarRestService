package ua.foxminded.carrestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import ua.foxminded.carrestservice.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, String>, JpaSpecificationExecutor<Car>{
}
