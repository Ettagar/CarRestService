package ua.foxminded.carrestservice.controller;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import ua.foxminded.carrestservice.service.CarCatalogParserService;

@Controller
@Profile("!test")
@RequiredArgsConstructor
public class CarParserController implements ApplicationRunner {
	public final CarCatalogParserService carCatalogParserService;

	@Override
	public void run(ApplicationArguments args) {
		carCatalogParserService.parseDataFromCsv();
	}
}
