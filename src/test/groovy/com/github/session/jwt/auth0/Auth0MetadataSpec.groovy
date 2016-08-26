package com.github.session.jwt.auth0

import com.github.session.jwt.SpringSessionJwt
import spock.lang.Specification

class Auth0MetadataSpec extends Specification {
    private Auth0Metadata auth0Metadata
    private SpringSessionJwt springSessionJwt

    void setup() {
        def metadata = new HashMap()
        metadata.put("app_metadata", "{permissions=[{app=assettracker, role=USER, company=FMC, organisation=WAS}, {app=well-life, role=USER, company=FMC, organisation=WAS}, {app=sds, role=USER, company=FMC, organisation=WAS}, {app=frm, role=USER, company=FMC, organisation=WAS}], roles=[ROLE_USER]}")
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

    def "Get permissions"() {
        when:
        def permissions = auth0Metadata.getPermissions()

        then:
        permissions.size() == 4
    }

    def "Get permissions when metadata is empty"() {
        given:
        def metadata = new HashMap()
        metadata.put("app_metadata", "")

        when:
        def permissions = auth0Metadata.getPermissions()

        then:
        1 * springSessionJwt.get() >> metadata
        permissions.size() == 0
    }

    def "Is authorized for application"() {
        when:
        def authorized = auth0Metadata.isAuthorized("assettracker")

        then:
        authorized
    }

    def "Is not authorized for application"() {
        when:
        def authorized = auth0Metadata.isAuthorized("test-app")

        then:
        !authorized
    }

    def "Is not authorized when no permissions are configured"() {
        given:
        def metadata = new HashMap()
        metadata.put("app_metadata", "")

        when:
        def authorized = auth0Metadata.isAuthorized("assettracker")

        then:
        1 * springSessionJwt.get() >> metadata
        !authorized
    }
}
