package com.github.session.jwt.web

import com.github.session.jwt.config.ConfigService
import spock.lang.Specification

import javax.servlet.http.HttpSession

class RequestUtilSpec extends Specification {
    private RequestUtil requestUtil
    private ConfigService config
    private HttpSession session

    void setup() {
        config = Mock(ConfigService)
        session = Mock(HttpSession)

        requestUtil = new RequestUtil(httpSession: session, config: config)
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
}
