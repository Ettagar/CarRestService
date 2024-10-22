package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Category;

@Getter
public class CategoriesTestData {
	private List<Category> categories;

	public void setUp() {
		Category category1 = Category.builder()
		.id(1L)
		.name("SUV")
		.build();

		Category category2 = Category.builder()
		.id(2L)
		.name("Sedan")
		.build();

		categories = Arrays.asList(category1, category2);
	}
}
