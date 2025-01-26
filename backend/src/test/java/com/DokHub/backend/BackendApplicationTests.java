package com.DokHub.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"youtube.api.key=${YOUTUBE_API_KEY:}"})
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
