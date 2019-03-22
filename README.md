# Spring Session JWT

[![Build Status](https://travis-ci.org/ThomasBem/spring-session-jwt.svg?branch=master)](https://travis-ci.org/ThomasBem/spring-session-jwt)

Integrates [Spring session](http://projects.spring.io/spring-session/) using [Redis](http://redis.io/) as session store, with [JWT](https://jwt.io/).  
It is created to easily be used in combination with [auth0](https://auth0.com/).

## Installation

For Spring-Boot 1.X use:

Gradle
```
compile('com.github.session.jwt:spring-session-jwt:2.0.1')
```

For Spring-Boot 2.X use:

@Gradle
 ```
 compile('com.github.session.jwt:spring-session-jwt:3.0.0')
 ```
 
 Going forward updates will only be made for 2.X compatibility.


## Usage

When the `spring.redis.host` property is empty, a local redis instance is configured and started with `localhost` and port `6379`.
See the class `LocalRedisConfig` for more detailed information.  

## Configuration

| Key | Description | Default value |
|-----|-------------|---------------|
| basic-auth.username | Basic authentication header username | |
| basic-auth.password | Basic authentication header password | |
| jwt-secret | Secret used to verify JWT (from auth0) | |
| jwt-session-key | The key used to store the jwt in the session | jwt |
| cookie-domain | The domain set for the generated cookies | |
| cookie-max-age | The max age in seconds? for the cookie | 36000 |
| jwt-issuer | The issuer of the JWT used to validate | |
