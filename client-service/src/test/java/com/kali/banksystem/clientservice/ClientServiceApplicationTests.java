package com.kali.banksystem.clientservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kali.banksystem.clientservice.dto.client.ClientRequest;
import com.kali.banksystem.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ClientServiceApplicationTests {

	static {
		// Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during connection testing
		LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
	}

	@Container
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:9.6.12")
			.withInitScript("sql/init.sql");


	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		postgreSQLContainer.start();
	}

	@Test
	void shouldRegisterNewClient() throws Exception {
		ClientRequest clientRequest = getClientRequest();
		String clientRequestString = objectMapper.writeValueAsString(clientRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/client")
						.contentType(MediaType.APPLICATION_JSON)
						.content(clientRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, clientRepository.findAll().size());
	}

	private ClientRequest getClientRequest() {
		return ClientRequest.builder()
				.address("123 Main st")
				.email("johndoe@example.com")
				.firstName("john")
				.lastName("doe")
				.dateOfBirth(new Date())
				.phoneNumber("1234567890")
				.password("password1234")
				.build();
	}
}

