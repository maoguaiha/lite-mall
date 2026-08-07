package com.macro.mall.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.model.SmsCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponAdminIT {

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
    void create_and_list() {
        SmsCoupon c = new SmsCoupon();
        c.setName("IT测试券");
        c.setAmount(new java.math.BigDecimal("10"));
        c.setMinPoint(new java.math.BigDecimal("50"));
        c.setPerLimit(1);
        c.setPublishCount(10);
        c.setUsableRange("ALL");
        c.setStartTime(new Date());
        c.setEndTime(new Date(System.currentTimeMillis() + 86400000L));

        RequestEntity<SmsCoupon> create = RequestEntity
                .post(URI.create("http://localhost:" + port + "/admin/coupon/create"))
                .header("Authorization", "Bearer " + token).body(c);
        ResponseEntity<String> cr = rest.exchange(create, String.class);
        assertEquals(200, cr.getStatusCodeValue());

        RequestEntity<Void> list = RequestEntity
                .get(URI.create("http://localhost:" + port + "/admin/coupon/list"))
                .header("Authorization", "Bearer " + token).build();
        ResponseEntity<String> lr = rest.exchange(list, String.class);
        assertTrue(lr.getBody().contains("IT测试券"));
    }
}
