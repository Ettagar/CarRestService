package ua.foxminded.carrestservice.mapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.foxminded.carrestservice.model.Manufacturer;
import ua.foxminded.carrestservice.model.ManufacturerMapperContext;
import ua.foxminded.carrestservice.model.dto.ManufacturerDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManufacturerMapper {
	public ManufacturerDto toDto(Manufacturer manufacturer) {
		return new ManufacturerDto(
				manufacturer.getId(),
				manufacturer.getName()
			    );
	}

	public Manufacturer toModel(ManufacturerDto manufacturerDto, ManufacturerMapperContext context) {
		return new Manufacturer(
				manufacturerDto.id(),
				manufacturerDto.name(),
				context.getCars()
				);
	}
}
