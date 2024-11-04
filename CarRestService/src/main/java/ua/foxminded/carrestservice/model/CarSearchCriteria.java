package ua.foxminded.carrestservice.model;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarSearchCriteria {
	private String manufacturer;
	private String model;
	private Integer minYear;
	private Integer maxYear;
	private List<String> categories;

	@Override
	public String toString() {
		return "CarSearchCriteria{" +
				"manufacturer='" + manufacturer + '\'' +
				", model='" + model + '\'' +
				", minYear=" + minYear +
				", maxYear=" + maxYear +
				", categories=" + categories +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CarSearchCriteria that = (CarSearchCriteria) o;
		return Objects.equals(manufacturer, that.manufacturer) &&
				Objects.equals(model, that.model) &&
				Objects.equals(minYear, that.minYear) &&
				Objects.equals(maxYear, that.maxYear) &&
				Objects.equals(categories, that.categories);
	}

	@Override
	public int hashCode() {
		return Objects.hash(manufacturer, model, minYear, maxYear, categories);
	}
}
