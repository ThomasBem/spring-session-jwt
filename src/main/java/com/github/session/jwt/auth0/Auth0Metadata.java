package com.github.session.jwt.auth0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.session.jwt.SpringSessionJwt;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class Auth0Metadata {

    private static final String APP_METADATA = "app_metadata";

    @Autowired
    private SpringSessionJwt springSessionJwt;

    public Optional<String> get() {
        Map<String, String> claims = springSessionJwt.get();
        String metadata = claims.get(APP_METADATA);
        if (StringUtils.isEmpty(metadata)) {
            return Optional.empty();
        } else {
            String json = JsonPath.parse(metadata.replaceAll("=", ":")).jsonString();
            return Optional.of(json);
        }
    }

    public List<Permission> getPermissions() {
        Optional<String> metadata = get();
        if (metadata.isPresent()) {
            try {
                Permissions permissions = new ObjectMapper().readValue(metadata.get(), Permissions.class);
                return permissions.getPermissions();
            } catch (IOException e) {
                log.error("Unable to parse permissions metadata", e);
            }
        }

        return Collections.emptyList();
    }

    public boolean isAuthorized(String appName) {
        List<Permission> permissions = getPermissions();
        if (permissions.size() == 0) {
            return false;
        }

        Optional<Permission> permissionAppName = permissions.stream().filter(permission -> permission.getApp().toLowerCase().equals(appName)).findFirst();
        return permissionAppName.isPresent();
    }
}
