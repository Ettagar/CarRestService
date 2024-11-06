package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.dto.CategoryDto;

@Getter
public class CategoriesTestData {
	private List<Category> categories;
	private List<CategoryDto> categoriesDtos;

	public void setUp() {
		Category category1 = Category.builder()
				.id(1L)
				.name("SUV")
				.build();

		Category category2 = Category.builder()
				.id(2L)
				.name("Sedan")
				.build();

		Category category3 = Category.builder()
				.id(3L)
				.name("Luxury")
				.build();

		categories = Arrays.asList(category1, category2, category3);

		categoriesDtos = categories.stream()
				.map(category -> new CategoryDto(
						category.getId(),
						category.getName()))
				.toList();
	}
}
