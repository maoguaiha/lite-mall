package com.macro.mall.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminLoginIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void login_returnsToken() {
        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "admin123");
        ResponseEntity<String> r = rest.postForEntity("http://localhost:" + port + "/admin/login", body, String.class);
        assertEquals(200, r.getStatusCodeValue());
        assertTrue(r.getBody().contains("\"data\""));
    }
}
