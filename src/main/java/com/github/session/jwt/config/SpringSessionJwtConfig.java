package com.github.session.jwt.config;

import com.github.session.jwt.SpringSessionJwt;
import com.github.session.jwt.auth0.Auth0Metadata;
import com.github.session.jwt.web.RequestUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.StringUtils;

@Configuration
public class SpringSessionJwtConfig {

    @Bean
    public SpringSessionConfig springSessionConfig() {
        return new SpringSessionConfig();
    }

    @Bean
    public Auth0Metadata auth0Metadata() {
        return new Auth0Metadata();
    }

    @Bean
    public RequestUtil requestUtil() {
        return new RequestUtil();
    }

    @Bean
    public SpringSessionJwt springSessionJwt() {
        return new SpringSessionJwt();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieMaxAge(springSessionConfig().getCookieMaxAge());

        if (!StringUtils.isEmpty(springSessionConfig().getCookieDomain())) {
            serializer.setDomainName(springSessionConfig().getCookieDomain());
        }
        return serializer;
    }

}
