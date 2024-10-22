package ua.foxminded.carrestservice.component;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import ua.foxminded.carrestservice.service.CarCatalogParserService;

@Controller
@RequiredArgsConstructor
public class CarDataLoader implements ApplicationRunner {
	public final CarCatalogParserService carCatalogParserService;

	@Override
	public void run(ApplicationArguments args) {
		carCatalogParserService.parseDataFromCsv();
	}
}
