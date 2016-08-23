package com.github.session.jwt.session

import com.auth0.Tokens
import com.github.session.jwt.config.SpringSessionConfig
import io.jsonwebtoken.JwtException
import spock.lang.Specification

import javax.servlet.http.HttpSession

class SpringSessionUtilSpec extends Specification {
    private HttpSession httpSession
    private SpringSessionConfig config
    private SpringSessionUtil springSessionUtil

    void setup() {
        httpSession = Mock(HttpSession)
        config = Mock(SpringSessionConfig)
        springSessionUtil = new SpringSessionUtil(httpSession: httpSession, config: config)
    }

    def "Missing Tokens in session"(){
        when:
        springSessionUtil.addJWT()

        then:
        thrown(JwtException)
    }

    def "add JWT to session"(){
        when:
        springSessionUtil.addJWT()

        then:
        1 * httpSession.getAttribute(_ as String) >> new Tokens("idToken", "", "", "")
        1 * config.jwtSessionKey >> "test-key"
        1 * httpSession.setAttribute(_ as String, _ as String)
    }
}
