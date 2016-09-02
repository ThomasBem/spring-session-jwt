package com.github.session.jwt;

import com.github.session.jwt.config.SpringSessionConfig;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EnableEncryptableProperties
@EnableRedisHttpSession
@Component
@Slf4j
public class SpringSessionJwt {

    private static final String CLAIM_EXPIRATION = "exp";
    private static final String CLAIM_ISSUER = "iss";

    @Autowired
    private SpringSessionConfig config;

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    public Map<String, String> get() {
        Optional<String> jwt = getJwt();
        if (jwt.isPresent()) {
            return get(jwt.get());
        } else {
            throw new JwtException("No jwt on session (session-key: " + config.getJwtSessionKey() + ")");
        }
    }

    public Map<String, String> get(String jwt) {
        byte[] key = Base64Utils.decodeFromUrlSafeString(config.getJwtSecret());
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
        Claims body = claims.getBody();

        Map<String, String> values = new HashMap<>();
        body.keySet().forEach(claimKey -> {
            Object value = body.get(claimKey);
            values.put(claimKey, value.toString());
        });

        return values;
    }

    public Optional<String> getJwt() {
        String jwt = getJwtString();
        if (validateJwt(jwt)) {
            return Optional.of(jwt);
        }

        return Optional.empty();
    }

    private boolean validateJwt(String jwt) {
        if (jwt == null) {
            return false;
        }

        try {
            byte[] key = Base64Utils.decodeFromUrlSafeString(config.getJwtSecret());
            Jws<Claims> jwtClaims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
            return validIssuer(jwt, jwtClaims) && validExpiration(jwt, jwtClaims);
        } catch (JwtException e) {
            log.warn("JWT is not valid", e);
        }
        return false;
    }

    private boolean validIssuer(String jwt, Jws<Claims> jwtClaims) {
        String issuer = jwtClaims.getBody().get(CLAIM_ISSUER, String.class);
        if (!StringUtils.isEmpty(issuer) && issuer.equals(config.getIssuer())) {
            log.warn("JWT issuer is not valid, jwt: {}", jwt);
            return true;
        } else {
            return false;
        }
    }

    private boolean validExpiration(String jwt, Jws<Claims> jwtClaims) {
        Claims claims = jwtClaims.getBody();
        if (claims.containsKey(CLAIM_EXPIRATION)) {
            Integer expiration = claims.get(CLAIM_EXPIRATION, Integer.class);
            if (expirationDatePassed(expiration)) {
                log.warn("JWT expiration date has passed, jwt: {}", jwt);
                return false;
            }
        }
        return true;
    }

    boolean expirationDatePassed(Integer expiration) {
        LocalDateTime jwtExpirationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(expiration), ZoneId.systemDefault());
        return jwtExpirationDate.isBefore(LocalDateTime.now());
    }

    private String getJwtString() {
        Object jwtObject = session.getAttribute(config.getJwtSessionKey());
        if (jwtObject == null) {
            return getJwtFromHeader();
        } else {
            return (String) jwtObject;
        }
    }

    private String getJwtFromHeader() {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (acceptHeader != null && acceptHeader.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            return request.getHeader(config.getJwtSessionKey());
        }

        return null;
    }

    public boolean isValidJwt() {
        Optional<String> jwt = getJwt();
        return jwt.isPresent();
    }
}