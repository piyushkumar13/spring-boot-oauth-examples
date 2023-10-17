/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclients.controller;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Piyush Kumar.
 * @since 02/10/23.
 */

@Data
@RestController
public class SimpleRestController {

    private static final String url = "http://localhost:8090/employee/authenticatedUsr";

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final WebClient webClient;

    @GetMapping("/test-client")
    public ResponseEntity<Object> getResponse(@RegisteredOAuth2AuthorizedClient("my-client-oidc") OAuth2AuthorizedClient oAuth2AuthorizedClient){

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }

    /**
     * By using webclient, we don't have to access accessToken manually and pass it with the http request.
     * Access token will be passed automatically behind the scene.
     * To pass accessToken automatically with ever request, this filter is used to make it happen :
     * {@link org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction}
     */
    @GetMapping("/test-web-client")
    public String getResponseUsingWebClient(){

        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    @GetMapping("/keyclock/return-token")
    public ResponseEntity<Object> getToken(@AuthenticationPrincipal OidcUser oidcUser){

        OidcIdToken idToken = oidcUser.getIdToken();
        System.out.println("IdToken ::: " + idToken);

        String idTokenValue = idToken.getTokenValue();
        System.out.println("IdToken value ::: " + idTokenValue);


        /* Following lines of code working can also be achieved by using @RegisteredOAuth2AuthorizedClient("client-piyush-service") annotation. */
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        OAuth2AccessToken accessToken = oAuth2AuthorizedClient.getAccessToken();
        System.out.println("AccessToken ::: " + accessToken);

        String accessTokenValue = accessToken.getTokenValue();
        System.out.println("AccessTokenValue ::: " + accessTokenValue);

        OAuth2RefreshToken refreshToken = oAuth2AuthorizedClient.getRefreshToken();
        System.out.println("RefreshToken ::: " + refreshToken);

        String refreshTokenValue = refreshToken.getTokenValue();
        System.out.println("RefreshTokenValue ::: " + refreshTokenValue);

        return ResponseEntity.ok(

            TokenDetails.builder()
                .idToken(idTokenValue)
                .accessToken(accessTokenValue)
                .refreshToken(refreshTokenValue)
                .build()
        );
    }

    @GetMapping("/facebook/return-token")
    public ResponseEntity<Object> getFacebookToken(@RegisteredOAuth2AuthorizedClient("facebook") OAuth2AuthorizedClient oAuth2AuthorizedClient){

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        String refreshToken = Optional.ofNullable(oAuth2AuthorizedClient.getRefreshToken()).map(OAuth2RefreshToken::getTokenValue).orElse(null);

        return ResponseEntity.ok(

            TokenDetails.builder()
                .idToken("")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build()
        );
    }

    @GetMapping("/google/return-token")
    public ResponseEntity<Object> getGoogleToken(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient oAuth2AuthorizedClient){

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        String refreshToken = Optional.ofNullable(oAuth2AuthorizedClient.getRefreshToken()).map(OAuth2RefreshToken::getTokenValue).orElse(null);

        return ResponseEntity.ok(

            TokenDetails.builder()
                .idToken("")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build()
        );
    }

    @GetMapping("/github/return-token")
    public ResponseEntity<Object> getGitHubToken(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient oAuth2AuthorizedClient){

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        String refreshToken = Optional.ofNullable(oAuth2AuthorizedClient.getRefreshToken()).map(OAuth2RefreshToken::getTokenValue).orElse(null);

        return ResponseEntity.ok(

            TokenDetails.builder()
                .idToken("")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build()
        );
    }

    @GetMapping("/azure/return-token")
    public ResponseEntity<Object> getAzureToken(@RegisteredOAuth2AuthorizedClient("my-azure") OAuth2AuthorizedClient oAuth2AuthorizedClient){

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        String refreshToken = Optional.ofNullable(oAuth2AuthorizedClient.getRefreshToken()).map(OAuth2RefreshToken::getTokenValue).orElse(null);

        return ResponseEntity.ok(

            TokenDetails.builder()
                .idToken("")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build()
        );
    }

    @Data
    @Builder
    public static final class TokenDetails {

        private String idToken;
        private String accessToken;
        private String refreshToken;
    }
}
