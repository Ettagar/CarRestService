package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Manufacturer;

@Getter
public class ManufacturerTestData {
	private List<Manufacturer> manufacturers;

	public void setUp() {
		Manufacturer manufacturer1 = new Manufacturer();
		manufacturer1.setId(1L);
		manufacturer1.setName("Audi");

		Manufacturer manufacturer2 = new Manufacturer();
		manufacturer2.setId(2L);
		manufacturer2.setName("Mazda");

		Manufacturer manufacturer3 = new Manufacturer();
		manufacturer3.setId(3L);
		manufacturer3.setName("VW");

		manufacturers = Arrays.asList(manufacturer1, manufacturer2, manufacturer3);
	}
}
