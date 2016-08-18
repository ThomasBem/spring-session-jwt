# Spring Session JWT

## Installation


Gradle
```
compile('com.github.session.jwt:spring-session-jwt:0.0.1')
```

```
repositories {
    maven {
        url  "http://dl.bintray.com/thomasbem/maven"
    }
}
```

## Usage

When the `spring.redis.host` property is empty, a local redis instance is configured and started with `localhost` and `6379`.
See the class `LocalRedisConfig` for more detailed information.

## Configuration

| Key | Description | Default value |
|-----|-------------|---------------|
| basic-auth.username | Basic authentication header username | |
| basic-auth.password | Basic authentication header password | |
| jwt-secret | Secret used to verify JWT (from auth0) | |
| jwt-session-key | The key used to store the jwt in the session | jwt |
| cookie-domain | The domain set for the generated cookies | |
