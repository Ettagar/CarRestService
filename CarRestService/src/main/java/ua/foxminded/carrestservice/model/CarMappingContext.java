package ua.foxminded.carrestservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarMappingContext {
	private final Manufacturer manufacturer;
	private final List<Category> categories;
}
