package ua.foxminded.carrestservice.repository;

import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import ua.foxminded.carrestservice.model.Car;
import ua.foxminded.carrestservice.model.Category;
import ua.foxminded.carrestservice.model.Manufacturer;

public class CarSpecification {
	private CarSpecification() {
		throw new IllegalStateException("Utility class");
	}

	public static Specification<Car> hasManufacturer(String manufacturerName) {
		return (root, query, criteriaBuilder) -> {
			if (manufacturerName == null || manufacturerName.isEmpty()) {
				return null;
			}

			Join<Car, Manufacturer> manufacturerJoin = root.join("manufacturer");

			return criteriaBuilder.equal(
					criteriaBuilder.lower(manufacturerJoin.get("name")),
					manufacturerName.toLowerCase()
					);
		};
	}

	public static Specification<Car> hasModel(String model) {
		return (root, query, criteriaBuilder) -> {
			if (model == null || model.isEmpty()) {
				return null;
			}

			return criteriaBuilder.equal(
					criteriaBuilder.lower(root.get("model")),
					model.toLowerCase()
					);
		};
	}

	public static Specification<Car> hasYear(Integer minYear, Integer maxYear) {
		return (root, query, criteriaBuilder) -> {
			if (minYear == null && maxYear == null) {
				return null;
			}
			if (minYear != null && maxYear != null) {
				if (minYear.equals(maxYear)) {
					return criteriaBuilder.equal(root.get("year"), minYear);
				}
				return criteriaBuilder.between(root.get("year"), minYear, maxYear);
			} else if (minYear != null) {
				return criteriaBuilder.greaterThanOrEqualTo(root.get("year"), minYear);
			} else {
				return criteriaBuilder.lessThanOrEqualTo(root.get("year"), maxYear);
			}
		};
	}

	public static Specification<Car> hasCategories(List<String> categories) {
		return (root, query, criteriaBuilder) -> {
			if (categories == null || categories.isEmpty()) {
				return null;
			}

			Join<Car, Category> categoryJoin = root.join("categories");

			return criteriaBuilder.lower(categoryJoin.get("name")).in(
					categories.stream()
					.filter(Objects::nonNull)
					.map(String::toLowerCase)
					.toList()
					);
		};
	}
}
