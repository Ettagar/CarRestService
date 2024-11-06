package ua.foxminded.carrestservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ua.foxminded.carrestservice.exception.ServiceException;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.repository.CategoriesRepository;
import ua.foxminded.carrestservice.util.CategoriesTestData;

class CategoryServiceTest {
	@Mock
	private CategoriesRepository categoriesRepository;

	@InjectMocks
	private CategoryService categoryService;

	private CategoriesTestData categoriesTestData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		categoriesTestData = new CategoriesTestData();
		categoriesTestData.setUp();
	}

	@Test
	void testCreateCategory_shouldReturnExistingCategory_whenCategoryAlreadyExists() throws ServiceException {
		Category existingCategory = categoriesTestData.getCategories().get(0);

		when(categoriesRepository.findByName(existingCategory.getName())).thenReturn(Optional.of(existingCategory));

		Category result = categoryService.createCategory(existingCategory.getName());

		assertEquals(existingCategory, result);
		verify(categoriesRepository, never()).save(ArgumentMatchers.any(Category.class));
	}

	@Test
	void testCreateCategory_shouldCreateAndReturnNewCategory_whenCategoryDoesNotExist() throws ServiceException {
		String newCategoryName = "NewCategory";
		Category newCategory = Category.builder().name(newCategoryName).build();

		when(categoriesRepository.findByName(newCategoryName)).thenReturn(Optional.empty());
		when(categoriesRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(newCategory);

		Category result = categoryService.createCategory(newCategoryName);

		assertEquals(newCategoryName, result.getName());
		verify(categoriesRepository).save(ArgumentMatchers.any(Category.class));
	}

	@Test
	void testCreateCategory_shouldThrowServiceException_whenRepositoryFails() {
		String categoryName = "FailCategory";

		when(categoriesRepository.findByName(categoryName)).thenThrow(new RuntimeException("Database error"));

		ServiceException exception = assertThrows(ServiceException.class,
				() -> categoryService.createCategory(categoryName));

		assertEquals("An error occurred while finding or creating the category", exception.getMessage());
	}

	@Test
	void testFindByName_shouldReturnCategory_whenCategoryExists() {
		Category existingCategory = categoriesTestData.getCategories().get(1);

		when(categoriesRepository.findByName(existingCategory.getName())).thenReturn(Optional.of(existingCategory));

		Optional<Category> result = categoryService.findByName(existingCategory.getName());

		assertTrue(result.isPresent());
		assertEquals(existingCategory, result.get());
	}

	@Test
	void testFindByName_shouldThrowException_whenCategoryNameIsInvalid() {
		assertThrows(IllegalArgumentException.class, () -> categoryService.findByName("  "));
		assertThrows(IllegalArgumentException.class, () -> categoryService.findByName((String) null));
	}

	@Test
	void testFindByNameList_shouldReturnCategories_whenAllNamesExist() {
		List<String> categoryNames = List.of("SUV", "Sedan");
		List<Category> expectedCategories = categoriesTestData.getCategories().stream()
				.filter(category -> categoryNames.contains(category.getName())).toList();

		when(categoriesRepository.findByName("SUV")).thenReturn(Optional.of(expectedCategories.get(0)));
		when(categoriesRepository.findByName("Sedan")).thenReturn(Optional.of(expectedCategories.get(1)));

		List<Category> result = categoryService.findByName(categoryNames);

		assertEquals(2, result.size());
		assertTrue(result.containsAll(expectedCategories));
	}

	@Test
	void testFindByNameList_shouldThrowException_whenCategoryNamesListIsNull() {
		assertThrows(IllegalArgumentException.class, () -> categoryService.findByName((List<String>) null));
	}

	@Test
	void testFindByNameList_shouldThrowException_whenCategoryNamesListIsEmpty() {
		assertThrows(IllegalArgumentException.class, () -> categoryService.findByName(List.of()));
	}

	@Test
	void testFindByNameList_shouldIgnoreNonexistentCategories() {
		List<String> categoryNames = List.of("SUV", "NonexistentCategory");
		Category existingCategory = categoriesTestData.getCategories().get(0);

		when(categoriesRepository.findByName("SUV")).thenReturn(Optional.of(existingCategory));
		when(categoriesRepository.findByName("NonexistentCategory")).thenReturn(Optional.empty());

		List<Category> result = categoryService.findByName(categoryNames);

		assertEquals(1, result.size());
		assertTrue(result.contains(existingCategory));
	}
}
