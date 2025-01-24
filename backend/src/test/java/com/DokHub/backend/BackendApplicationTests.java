package com.DokHub.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@SpringBootTest
@TestPropertySource(properties = {"youtube.api.key=test_api_key"})
class BackendApplicationTests {

	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		restTemplate = mock(RestTemplate.class); // RestTemplate을 Mockito로 모킹
	}

	@Test
	void contextLoads() {
		// 테스트 로직
	}
}
