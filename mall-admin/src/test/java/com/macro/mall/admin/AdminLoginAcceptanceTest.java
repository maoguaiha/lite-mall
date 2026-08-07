package com.macro.mall.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:test_admin;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "jwt.secret=mall-admin-secret-key-2024-must-be-long-enough-for-hmac-sha256-sign",
        "jwt.expire=3600000"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminLoginAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String token;

    @BeforeEach
    void login() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "admin123");
        ResponseEntity<String> r = rest.postForEntity("http://localhost:" + port + "/admin/login", body, String.class);
        Map<?, ?> resp = new ObjectMapper().readValue(r.getBody(), Map.class);
        token = (String) resp.get("data");
    }

    @Test
    void dashboard_requiresAndAcceptsToken() {
        RequestEntity<Void> req = RequestEntity
                .get(URI.create("http://localhost:" + port + "/admin/dashboard/stats"))
                .header("Authorization", "Bearer " + token)
                .build();
        ResponseEntity<String> r = rest.exchange(req, String.class);
        assertEquals(200, r.getStatusCodeValue());
        assertTrue(r.getBody().contains("totalSales"));
    }

    @Test
    void dashboard_rejectsWithoutToken() {
        ResponseEntity<String> r = rest.getForEntity("http://localhost:" + port + "/admin/dashboard/stats", String.class);
        assertNotEquals(200, r.getStatusCodeValue());
    }
}
