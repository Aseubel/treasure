package com.aseubel.treasure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TreasureApplicationTests {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Test
    void contextLoads() {
        System.out.println("Active profile: " + activeProfile);
        System.out.println("Active profile: " + System.getenv("SPRING_PROFILES_ACTIVE"));
    }

}
