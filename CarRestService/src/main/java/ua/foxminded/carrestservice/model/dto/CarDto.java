package ua.foxminded.carrestservice.model.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarDto (
		String id,

		@NotBlank(message = "Manufacturer is required")
		String manufacturer,

		@NotBlank(message = "Model is required")
		@Length(max = 40, message = "Max model name length is 40")
		String model,

		@NotNull(message = "Year is required")
		@Min(value = 1886, message = "Year must be later than 1885")
		Integer year,

		@Size(min = 1, message = "At least one category is required")
		List<String> categories
		) {
	public CarDto(
			@NotBlank(message = "Manufacturer is required") String manufacturer,
			@NotBlank(message = "Model is required") String model,
			@NotNull(message = "Year is required") Integer year,
			@Size(min = 1, message = "At least one category is required") List<String> categories) {

		this(null, manufacturer, model, year, categories);
	}
}
