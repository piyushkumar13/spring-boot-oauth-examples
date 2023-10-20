/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientscustomization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Piyush Kumar.
 * @since 02/10/23.
 */

@EnableWebSecurity(debug = true)
@Configuration
public class Configs {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Webclient will pass access token automatically with every http request.
     * Webclient to pass accessToken automatically with every request, this filter is used to make it happen :
     * {@link ServletOAuth2AuthorizedClientExchangeFilterFunction}
     */
    @Bean
    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
                        OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauthFilterFunction
            = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, oAuth2AuthorizedClientRepository);

        oauthFilterFunction.setDefaultOAuth2AuthorizedClient(true);

        return WebClient.builder().apply(oauthFilterFunction.oauth2Configuration()).build();
    }

    /**
     * TODO : Comment out below config if dont want PKCE with secret clients.
     * Reference video for the following config : https://www.youtube.com/watch?v=mKvi9RWGn3M
     * Following config is for PKCE with secret client. If we dont want PKCE with secret client, we will have to comment the following config.
     * OR we can comment this line resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
     * OR we can change setting up of authoriationRequestCustomizer as resolver.setAuthorizationRequestCustomizer(customizer -> {}); which does nothing.
     *
     * Refer class and its fields {@link DefaultOAuth2AuthorizationRequestResolver},
     * {@link DefaultOAuth2AuthorizationRequestResolver#authorizationRequestCustomizer},
     * {@link DefaultOAuth2AuthorizationRequestResolver#DEFAULT_PKCE_APPLIER}
     */
    @Bean
    public SecurityFilterChain configureFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {

        String uri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, uri);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .oauth2Login(oauth2Login -> {
                oauth2Login.authorizationEndpoint().authorizationRequestResolver(resolver);
            })
            .oauth2Client(Customizer.withDefaults())
            .build();
    }
}
