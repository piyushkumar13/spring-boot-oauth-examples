/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclients.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Piyush Kumar.
 * @since 02/10/23.
 */

@Data
@RestController
public class SimpleRestController {

    private static final String url = "http://localhost:8090/employee/authenticatedUsr";

    private final RestTemplate restTemplate;

    @GetMapping("/test-client")
    public ResponseEntity<Object> getResponse(@RegisteredOAuth2AuthorizedClient("my-client-oidc") OAuth2AuthorizedClient oAuth2AuthorizedClient){

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }

}
