package com.github.session.jwt.session;

import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.github.session.jwt.config.SpringSessionConfig;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;

@Component
public class SpringSessionUtil {

    @Autowired
    private SpringSessionConfig config;

    @Autowired
    private HttpSession httpSession;

    public void addJwt() {
        Tokens tokens = (Tokens) httpSession.getAttribute(SessionUtils.TOKENS);
        if (tokens == null || StringUtils.isEmpty(tokens.getIdToken())) {
            throw new JwtException("No token stored in session");
        } else {
            httpSession.setAttribute(config.getJwtSessionKey(), tokens.getIdToken());
        }
    }
}
