package com.github.session.jwt.testutils;

import com.github.session.jwt.annotations.EnableSpringSessionJwt;
import com.github.session.jwt.config.SpringSessionConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
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

    @Bean
    public HttpServletRequest httpServletRequest() {
        return new MockHttpServletRequest();
    }

    @Bean
    public SpringSessionConfig springSessionConfig() { return new SpringSessionConfig(); }

    @Bean
    public HttpSession session() {
        return new MockHttpSession();
    }

    @Bean
    public HttpServletRequest request() {
        return new MockHttpServletRequest();
    }
}
