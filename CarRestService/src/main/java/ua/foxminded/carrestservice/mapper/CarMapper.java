package ua.foxminded.carrestservice.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.dto.CarDto;

@Mapper(componentModel = "spring", uses = { ManufacturerMapper.class, CategoryMapper.class })
public interface CarMapper {

	@Mapping(target = "manufacturer", source = "manufacturer.name")
	@Mapping(target = "categories", expression = "java(mapCategoriesToStrings(car.getCategories()))")
	CarDto toDto(Car car);

	List<CarDto> toDtoList(Collection<Car> cars);

	default List<String> mapCategoriesToStrings(List<Category> categories) {

		return categories.stream().map(Category::getName).collect(Collectors.toList());
    }
}
