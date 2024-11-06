package ua.foxminded.carrestservice.util;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;

@Getter
public class ManufacturerTestData {
	private List<Manufacturer> manufacturers;
	private List<ManufacturerDto> manufacturersDtos;

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

		manufacturersDtos = manufacturers.stream()
				.map(manufacturer ->  new ManufacturerDto(
						manufacturer.getId(),
						manufacturer.getName()))
				.toList();
	}
}
