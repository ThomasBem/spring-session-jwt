package com.github.session.jwt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Configuration
public class LocalRedisConfig {

    private static final String defaultHost = "localhost";
    private static final int defaultPort = 6379;

    @Value("${spring.redis.host:}")
    private String redisHost;

    private RedisServer redisServer;

    @PostConstruct
    public void init() throws IOException {
        if (StringUtils.isEmpty(redisHost)) {
            System.setProperty("spring.redis.host", defaultHost);
            System.setProperty("spring.redis.port", String.valueOf(6379));
            log.info("Starting local redis server on port {}", defaultPort);
            redisServer = new RedisServer(defaultPort);
            redisServer.start();
        }
    }

    @PreDestroy
    public void shutdown() {
        if (redisServer.isActive()) {
            redisServer.stop();
        }
    }
}