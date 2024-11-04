package ua.foxminded.carrestservice.mapper;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

@Component
public class PageMapper {
	public static <D, T> Page<D> mapEntityPageToDtoPage(Page<T> entities, Function<T, D> mapper) {
		List<D> dtoList = entities.getContent()
				.stream()
				.map(mapper)
				.toList();

		return new PageImpl<>(dtoList, entities.getPageable(), entities.getTotalElements());
	}
}
