package ua.foxminded.carrestservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.dto.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	CategoryDto toDto(Category category);

	List<CategoryDto> toDtoList(List<Category> categories);
}
