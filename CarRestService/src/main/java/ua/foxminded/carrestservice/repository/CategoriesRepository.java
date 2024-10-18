package ua.foxminded.carrestservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.foxminded.carrestservice.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByName(String name);
}
