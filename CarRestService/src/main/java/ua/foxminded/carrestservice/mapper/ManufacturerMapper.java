package ua.foxminded.carrestservice.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

	ManufacturerDto toDto(Manufacturer manufacturer);

	List<ManufacturerDto> toDtoList(Collection<Manufacturer> manufacturer);
}
