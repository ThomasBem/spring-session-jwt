package com.github.session.jwt.testutils;

import com.github.session.jwt.annotations.EnableSpringSessionJwt;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@EnableSpringSessionJwt
@SpringBootApplication
public class TestApplication {

    @Bean
    public HttpSession httpSession() {
        return new MockHttpSession();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
