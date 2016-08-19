package com.github.session.jwt.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ConfigService {

    @Value("${basic-auth.username:}")
    private String username;

    @Value("${basic-auth.password:}")
    private String password;

    @Value("${jwt-secret:}")
    private String jwtSecret;

    @Value("${jwt-session-key:jwt}")
    private String jwtSessionKey;

    @Value("${cookie-domain:}")
    private String cookieDomain;

    @Value("${cookie-max-age:36000}")
    private int cookieMaxAge;
}
