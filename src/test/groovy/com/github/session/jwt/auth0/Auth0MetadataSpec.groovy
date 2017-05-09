package com.github.session.jwt.auth0

import com.github.session.jwt.SpringSessionJwt
import spock.lang.Specification

class Auth0MetadataSpec extends Specification {
    private Auth0Metadata auth0Metadata
    private SpringSessionJwt springSessionJwt

    void setup() {
        def metadata = new HashMap()
        metadata.put("app_metadata", "{roles=[USER_ROLE], applications=[{name=my-app1, environments=[{environment=development, claims={role=user, company=my-company1, organisation=my-org1}}, {environment=demo, claims={role=user, company=my-company1, organisation=my-org1}}, {environment=production, claims={role=user, company=my-company1, organisation=my-org1}}]}, {name=my-app2, environments=[{environment=development, claims={role=user, company=my-company2, organisation=my-org2}}, {environment=demo, claims={role=user, company=my-company2, organisation=my-org2}}, {environment=production, claims={role=user, company=my-company2, organisation=my-org2}}]}]}")
        springSessionJwt = Mock(SpringSessionJwt) {
            get() >> metadata
        }
        auth0Metadata = new Auth0Metadata(springSessionJwt: springSessionJwt)
    }

    def "Get metadata"() {
        when:
        def metadata = auth0Metadata.get()

        then:
        metadata.isPresent()
    }

    def "Get applications"() {
        when:
        def applications = auth0Metadata.getApplications()

        then:
        applications.size() == 2
    }

    def "Get empty list when metadata is empty"() {
        given:
        def metadata = new HashMap()
        metadata.put("app_metadata", "")

        when:
        def applications = auth0Metadata.getApplications()

        then:
        1 * springSessionJwt.get() >> metadata
        applications.size() == 0
    }

    def "Is authorized"() {
        when:
        def authorized = auth0Metadata.isAuthorized("my-app1", "development")

        then:
        authorized
    }

    def "Is not authorized"() {
        when:
        def authorized = auth0Metadata.isAuthorized("test-app", "demo")

        then:
        !authorized
    }

    def "Is not authorized when no permissions are configured"() {
        given:
        def metadata = new HashMap()
        metadata.put("app_metadata", "")

        when:
        def authorized = auth0Metadata.isAuthorized("my-app1", "demo")

        then:
        1 * springSessionJwt.get() >> metadata
        !authorized
    }
}
