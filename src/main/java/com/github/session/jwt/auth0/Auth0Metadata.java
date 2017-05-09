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

    public boolean isAuthorized(String appName, String environment) {
        List<Application> applications = getApplications();
        if (applications.size() == 0) {
            return false;
        }

        Optional<Application> application = applications.stream().filter(app -> app.getName().toLowerCase().equals(appName)).findFirst();
        if(application.isPresent()) {
            Application approvedApplication = application.get();
            Optional<Environment> approvedEnvironment = approvedApplication.getEnvironments().stream().filter(env -> env.getEnvironment().toLowerCase().equals(environment)).findFirst();
            if(approvedEnvironment.isPresent()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<Application> getApplications() {
        Optional<String> metadata = get();
        if (metadata.isPresent()) {
            try {
                Applications applications = new ObjectMapper().readValue(metadata.get(), Applications.class);
                return applications.getApplications();
            } catch (IOException e) {
                log.error("Unable to parse applications metadata", e);
            }
        }

        return Collections.emptyList();
    }
}
