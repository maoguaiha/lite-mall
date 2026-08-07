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
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test_portal;DB_CLOSE_DELAY=-1;MODE=MySQL"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeckillAcceptanceTest {

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
        reg.put("username", "skuac");
        reg.put("password", "p");
        reg.put("phone", "13800000003");
        reg.put("nickname", "n");
        rest.postForEntity("http://localhost:" + port + "/member/register", reg, String.class);

        Map<String, String> login = new HashMap<>();
        login.put("username", "skuac");
        login.put("password", "p");
        ResponseEntity<String> r = rest.postForEntity("http://localhost:" + port + "/member/login", login, String.class);
        Map<?, ?> resp = new ObjectMapper().readValue(r.getBody(), Map.class);
        token = (String) resp.get("data");

        // Redis mock：init 用 opsForValue/delete；buy 第一次成功、第二次"已参与"
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.delete(anyString())).thenReturn(true);
        when(redisTemplate.execute(any(), any(), any())).thenReturn(1L, 2L);
    }

    @Test
    void buy_once_ok_then_idempotentReject() {
        RequestEntity<Void> init = RequestEntity
                .post(URI.create("http://localhost:" + port + "/seckill/init?seckillProductId=1"))
                .header("Authorization", "Bearer " + token).build();
        assertEquals(200, rest.exchange(init, String.class).getStatusCodeValue());

        RequestEntity<Void> buy = RequestEntity
                .post(URI.create("http://localhost:" + port + "/seckill/buy?seckillProductId=1"))
                .header("Authorization", "Bearer " + token).build();

        ResponseEntity<String> first = rest.exchange(buy, String.class);
        assertEquals(200, first.getStatusCodeValue());

        ResponseEntity<String> second = rest.exchange(buy, String.class);
        assertTrue(second.getBody().contains("已参与"));
    }
}
