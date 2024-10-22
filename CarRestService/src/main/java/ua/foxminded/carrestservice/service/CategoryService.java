package ua.foxminded.carrestservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.exception.ServiceException;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.repository.CategoriesRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
	private final CategoriesRepository categoriesRepository;

	@Transactional
	public Category createCategory(String categoryName) throws ServiceException {
		try {
			Optional<Category> existingCategory = categoriesRepository.findByName(categoryName);

			if (existingCategory.isPresent()) {
				return existingCategory.get();
			}

			Category newCategory = Category.builder().name(categoryName).build();
			return categoriesRepository.save(newCategory);
		} catch (Exception e) {
			log.error("An error occurred while finding or creating the category", e);
			throw new ServiceException("An error occurred while finding or creating the category", e);
		}
	}

	@Transactional
	public Optional<Category> findByName(String categoryName) {
		validateCategoryName(categoryName);

		return categoriesRepository.findByName(categoryName);
	}

	@Transactional
	public List<Category> findByName(List<String> categoriesNames) {
        if (categoriesNames == null || categoriesNames.isEmpty()) {
            throw new IllegalArgumentException("categories names list must not be null or empty");
        }

        return categoriesNames.stream()
        		 .map(this::findByName)
                 .filter(Optional::isPresent)
                 .map(Optional::get)
                 .collect(Collectors.toList());
    }

	private void validateCategoryName(String categoryName) {
		if (categoryName == null) {
			log.error("Category name must not be null");
			throw new IllegalArgumentException("Category name must not be null");
		}

		if (categoryName.trim().isEmpty()) {
			log.error("Category name must not be empty");
			throw new IllegalArgumentException("Category name must not be empty");
		}
	}
}

