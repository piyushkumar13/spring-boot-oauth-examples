package com.example.springbootoauthauthorizationserver;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;


/**
 *
 * http://localhost:8000/oauth2/authorize?response_type=code&client_id=client1&redirect_uri=http://127.0.0.1:8080/authorized&scope=openid read
 * */
@SpringBootApplication
public class SpringBootOauthAuthServerInmemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOauthAuthServerInmemoryApplication.class, args);
    }
}
