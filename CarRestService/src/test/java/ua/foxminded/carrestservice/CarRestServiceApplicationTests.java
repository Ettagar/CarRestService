package ua.foxminded.carrestservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class CarRestServiceApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
	.withDatabaseName("cars_database")
	.withUsername("cars_manager")
	.withPassword("password")
	.withReuse(false);

	@BeforeAll
	public static void setUp() {
		// Set properties for DB connection
		System.setProperty("DB_PORT", postgres.getMappedPort(5432).toString());
		System.setProperty("DB_USERNAME", postgres.getUsername());
		System.setProperty("DB_PASSWORD", postgres.getPassword());
	}

	@Test
	void contextLoads() {
	}
}
