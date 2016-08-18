package com.github.session.jwt

import com.github.session.jwt.testutils.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

@SpringApplicationConfiguration(TestApplication)
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
