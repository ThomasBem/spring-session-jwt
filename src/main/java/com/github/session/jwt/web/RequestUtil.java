package com.github.session.jwt.web;

import com.github.session.jwt.config.ConfigService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Component
public class RequestUtil {

    @Autowired
    private ConfigService config;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpSession httpSession;

    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, createRequestEntity(), responseType);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            log.warn("Http client error, url:{} status-code:{} message:{}", url, e.getStatusCode(), e.getMessage());
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    private HttpEntity createRequestEntity() {
        HttpHeaders header = createAuthorizationHeader();
        Optional<String> jwtToken = getTokenFromSession();
        if (jwtToken.isPresent()) {
            header.add("jwt", jwtToken.get());
        }
        return new HttpEntity<>(null, header);
    }

    HttpHeaders createAuthorizationHeader() {
        HttpHeaders header = new HttpHeaders();
        if (!StringUtils.isEmpty(config.getUsername()) && !StringUtils.isEmpty(config.getPassword())) {
            String authString = config.getUsername() + ":" + config.getPassword();
            String encoded = Base64.encodeBase64String(authString.getBytes());
            String authorization = "Basic " + encoded;
            header.set("Authorization", authorization);
        }
        return header;
    }

    Optional<String> getTokenFromSession() {
        try {
            String jwt = (String) httpSession.getAttribute(config.getJwtSessionKey());
            return validateToken(jwt) ? Optional.of(jwt) : Optional.empty();
        } catch(IllegalStateException e) {
            log.warn("Unable to get attribute from session, skipping jwt token", e);
            return Optional.empty();
        }
    }

    private boolean validateToken(String jwt) {
        if (StringUtils.isEmpty(jwt)) {
            return false;
        }

        try {
            byte[] key = Base64Utils.decodeFromUrlSafeString(config.getJwtSecret());
            Jwts.parser().setSigningKey(key).parse(jwt);
            return true;
        } catch (JwtException e) {
            log.error("Exception when parsing jwt", e);
            throw e;
        }
    }
}
