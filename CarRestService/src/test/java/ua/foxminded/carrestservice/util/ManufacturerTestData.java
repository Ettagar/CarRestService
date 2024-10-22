package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Manufacturer;

@Getter
public class ManufacturerTestData {
	private List<Manufacturer> manufacturers;

	public void setUp() {
		Manufacturer manufacturer1 = Manufacturer.builder()
		.id(1L)
		.name("Audi")
		.build();

		Manufacturer manufacturer2 = Manufacturer.builder()
		.id(2L)
		.name("Mazda")
		.build();

		Manufacturer manufacturer3 = Manufacturer.builder()
		.id(3L)
		.name("VW")
		.build();

		manufacturers = Arrays.asList(manufacturer1, manufacturer2, manufacturer3);
	}
}
