package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Category;

@Getter
public class CategoriesTestData {
	private List<Category> categories;

	public void setUp() {
		Category category1 = new Category();
		category1.setId(1L);
		category1.setName("SUV");

		Category category2 = new Category();
		category2.setId(2L);
		category2.setName("Sedan");

		categories = Arrays.asList(category1, category2);
	}
}
