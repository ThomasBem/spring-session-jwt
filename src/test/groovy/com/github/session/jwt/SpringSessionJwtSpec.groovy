package com.github.session.jwt

import com.github.session.jwt.config.SpringSessionConfig
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.util.Base64Utils
import spock.lang.Specification

import javax.servlet.http.HttpSession

class SpringSessionJwtSpec extends Specification {
    private SpringSessionJwt springSessionJwt
    private HttpSession httpSession
    private SpringSessionConfig config

    void setup() {
        httpSession = Mock(HttpSession)
        config = Mock(SpringSessionConfig)
        springSessionJwt = new SpringSessionJwt(httpSession: httpSession, config: config)
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
        1 * httpSession.getAttribute(_ as String) >> "eyJhbGciOiJIUzI1NiJ9.eyJ0ZXN0LWtleSI6InRlc3QtdmFsdWUifQ.ytAJ6xW-b7gNJdB49Sqfg7tZDSZORKkLBropODtda0k"
        1 * config.jwtSecret >> Base64Utils.encodeToUrlSafeString("test-key".bytes)
        claims.size() == 1
        claims.keySet().first() == "test-key"
    }

    def "Get jwt from response header"() {
        given:
        def headers = Mock(HttpHeaders)
        def response = Mock(ResponseEntity)

        when:
        def claims = springSessionJwt.get(response)

        then:
        1 * headers.getFirst(_ as String) >> "eyJhbGciOiJIUzI1NiJ9.eyJ0ZXN0LWtleSI6InRlc3QtdmFsdWUifQ.ytAJ6xW-b7gNJdB49Sqfg7tZDSZORKkLBropODtda0k"
        1 * response.getHeaders() >> headers
        1 * config.getJwtSessionKey() >> "jwt"
        1 * config.jwtSecret >> Base64Utils.encodeToUrlSafeString("test-key".bytes)
        claims.size() == 1
        claims.keySet().first() == "test-key"
    }

}
