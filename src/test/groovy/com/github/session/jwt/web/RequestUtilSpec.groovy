package com.github.session.jwt.web

import com.github.session.jwt.config.ConfigService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import javax.servlet.http.HttpSession

class RequestUtilSpec extends Specification {
    private RequestUtil requestUtil
    private RestTemplate restTemplate
    private ConfigService config
    private HttpSession session

    void setup() {
        restTemplate = Mock(RestTemplate)
        config = Mock(ConfigService)
        session = Mock(HttpSession)

        requestUtil = new RequestUtil(restTemplate: restTemplate, httpSession: session, config: config)
    }

    def "Get token from session"() {
        when:
        def token = requestUtil.getTokenFromSession()

        then:
        1 * config.getJwtSessionKey() >> "jwt"
        1 * config.getJwtSecret() >> "test-key"
        1 * session.getAttribute(_ as String) >> "eyJhbGciOiJIUzI1NiJ9.dGVzdA.ek6s7p95e0fvUxV-Irwq1OzoBR-xDE7wEh9M-CazulY"
        token != null
    }

    def "Create empty header"() {
        when:
        def header = requestUtil.createAuthorizationHeader()

        then:
        header.size() == 0
    }

    def "Create authorization header"() {
        when:
        def header = requestUtil.createAuthorizationHeader()

        then:
        2 * config.username >> "user"
        2 * config.password >> "password"
        header.size() == 1
    }

    def "Get request"() {
        when:
        def response = requestUtil.get("http://localhost:8080", String)

        then:
        1 * restTemplate.exchange(_ as String, _ as HttpMethod, _ as HttpEntity, _ as Class) >> ResponseEntity.ok("test")
        response.statusCode == HttpStatus.OK
        response.getBody() == "test"
    }
}
