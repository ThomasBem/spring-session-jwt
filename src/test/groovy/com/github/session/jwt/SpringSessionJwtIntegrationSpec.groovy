package com.github.session.jwt

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@IntegrationTest
class SpringSessionJwtIntegrationSpec extends Specification {

    @Autowired
    private SpringSessionJwt springSessionJwt

    def "Enable spring session JWT"() {
        when:
        springSessionJwt.toString()

        then:
        springSessionJwt != null
    }
}
