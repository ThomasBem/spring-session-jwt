package com.github.session.jwt

import com.github.session.jwt.config.SpringSessionConfig
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.Base64Utils
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

class SpringSessionJwtSpec extends Specification {
    private String tokenString = "eyJhbGciOiJIUzI1NiJ9.eyJ0ZXN0LWtleSI6InRlc3QtdmFsdWUiLCJpc3MiOiJ0ZXN0LWlzc3VlciJ9.TMyOCKI9q15qELmFNU4GEnQhQnvPo7uxeUV1tJ1W7L0"

    private SpringSessionJwt springSessionJwt
    private HttpSession httpSession
    private SpringSessionConfig config
    private HttpServletRequest request

    void setup() {
        httpSession = Mock(HttpSession)
        config = Mock(SpringSessionConfig) {
            getIssuer() >> "test-issuer"
        }
        request = Mock(HttpServletRequest)
        springSessionJwt = new SpringSessionJwt(session: httpSession, config: config, request: request)
    }

    def "No jwt"() {
        when:
        springSessionJwt.get()

        then:
        2 * config.getJwtSessionKey() >> "jwt"
        1 * httpSession.getAttribute(_ as String) >> null
        thrown(JwtException)
    }

    def "Get jwt"() {
        when:
        def claims = springSessionJwt.get()

        then:
        1 * config.getJwtSessionKey() >> "jwt"
        1 * httpSession.getAttribute(_ as String) >> tokenString
        2 * config.jwtSecret >> Base64Utils.encodeToUrlSafeString("test-key".bytes)
        claims.size() == 2
    }

    def "Get jwt from session"() {
        when:
        def jwt = springSessionJwt.getJwt()

        then:
        1 * config.getJwtSessionKey() >> "test-key"
        1 * httpSession.getAttribute(_ as String) >> tokenString
        1 * config.getJwtSecret() >> Base64Utils.encodeToUrlSafeString("test-key".bytes)
        jwt.isPresent()
    }

    def "Return null from session, no jwt stored"() {
        when:
        def jwt = springSessionJwt.getJwt()

        then:
        1 * config.getJwtSessionKey() >> "test-key"
        1 * request.getHeader(_ as String) >> null
        1 * httpSession.getAttribute(_ as String) >> null
        !jwt.isPresent()
    }

    def "No jwt stored in session, get from header"() {
        when:
        def jwt = springSessionJwt.getJwt()

        then:
        2 * config.getJwtSessionKey() >> "test-key"
        1 * request.getHeader(HttpHeaders.ACCEPT) >> MediaType.APPLICATION_JSON_VALUE
        1 * request.getHeader("test-key") >> tokenString
        1 * config.getJwtSecret() >> Base64Utils.encodeToUrlSafeString("test-key".bytes)
        jwt.isPresent()
    }

    def "Expiration date has passed"() {
        when:
        def passed = springSessionJwt.expirationDatePassed(1472747771)

        then:
        passed
    }

    def "Not valid issuer"() {
        when:
        def jwt = springSessionJwt.getJwt()

        then:
        1 * config.getIssuer() >> ""
        1 * config.getJwtSessionKey() >> "test-key"
        1 * httpSession.getAttribute(_ as String) >> tokenString
        1 * config.getJwtSecret() >> Base64Utils.encodeToUrlSafeString("test-key".bytes)
        !jwt.isPresent()
    }
}
