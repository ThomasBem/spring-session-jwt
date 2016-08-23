package com.github.session.jwt.config;

import com.github.session.jwt.SpringSessionJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.StringUtils;

@ComponentScan(basePackageClasses = SpringSessionJwt.class)
@Configuration
public class SpringSessionJwtConfig {

    @Autowired
    private SpringSessionConfig config;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieMaxAge(config.getCookieMaxAge());

        if (!StringUtils.isEmpty(config.getCookieDomain())) {
            serializer.setDomainName(config.getCookieDomain());
        }
        return serializer;
    }

}
