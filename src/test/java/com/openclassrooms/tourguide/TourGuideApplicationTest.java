package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;

@SpringBootTest
class TourGuideApplicationTest {

	@Test
	void contextLoads() {
		assertEquals("6.1.5",SpringVersion.getVersion());
	}

}
