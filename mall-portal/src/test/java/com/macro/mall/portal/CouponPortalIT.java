package com.macro.mall.portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test_portal;DB_CLOSE_DELAY=-1;MODE=MySQL"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponPortalIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @MockBean
    StringRedisTemplate redisTemplate;

    private String token;

    @BeforeEach
    void prepare() throws Exception {
        Map<String, String> reg = new HashMap<>();
        reg.put("username", "cuit");
        reg.put("password", "p");
        reg.put("phone", "13800000000");
        reg.put("nickname", "n");
        rest.postForEntity("http://localhost:" + port + "/member/register", reg, String.class);

        Map<String, String> login = new HashMap<>();
        login.put("username", "cuit");
        login.put("password", "p");
        ResponseEntity<String> r = rest.postForEntity("http://localhost:" + port + "/member/login", login, String.class);
        Map<?, ?> resp = new ObjectMapper().readValue(r.getBody(), Map.class);
        token = (String) resp.get("data");
    }

    @Test
    void receive_and_center() {
        RequestEntity<Void> recv = RequestEntity
                .post(URI.create("http://localhost:" + port + "/coupon/receive?couponId=1"))
                .header("Authorization", "Bearer " + token).build();
        assertEquals(200, rest.exchange(recv, String.class).getStatusCodeValue());

        RequestEntity<Void> center = RequestEntity
                .get(URI.create("http://localhost:" + port + "/coupon/center"))
                .header("Authorization", "Bearer " + token).build();
        String body = rest.exchange(center, String.class).getBody();
        assertTrue(body.contains("新人满100减20"));
    }
}
