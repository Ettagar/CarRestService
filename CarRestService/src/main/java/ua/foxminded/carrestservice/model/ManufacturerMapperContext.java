package ua.foxminded.carrestservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ManufacturerMapperContext {
	 private final List<Car> cars;
}
