package ua.foxminded.carrestservice.model.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record ManufacturerDto (
		Long id,

		@NotBlank(message = "Manufacturer Name is required")
		@Length(max = 40, message = "Max name length is 40")
		String name
		) {
}
