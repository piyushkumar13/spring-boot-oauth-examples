/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientscustomization.config;

import static com.example.springbootoauthclientscustomization.customization.CustomAuthorizationRequestRepository.OAUTH_COOKIE_NAME;
import static com.example.springbootoauthclientscustomization.customization.CustomAuthorizationRequestRepository.AUTHENTICATION_OBJ;
import static com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper.OAUTH_COOKIE_EXPIRY;

import com.example.springbootoauthclientscustomization.customization.CustomSavedRequestAwareAuthenticationSuccessHandler;
import com.example.springbootoauthclientscustomization.customization.SetSecurityContextFilter;
import com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper;
import com.example.springbootoauthclientscustomization.customization.helpers.EncryptionHelper;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.SerializationUtils;
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

    @Bean
    public SetSecurityContextFilter setSecurityContextFilter(){
        return new SetSecurityContextFilter();
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
    public SecurityFilterChain configureFilterChain(HttpSecurity http,
                                                    AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) throws Exception {

        RequestCache requestCache = new CookieRequestCache();

        CustomSavedRequestAwareAuthenticationSuccessHandler handler = new CustomSavedRequestAwareAuthenticationSuccessHandler();
        handler.setRequestCache(requestCache);

        return http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2Login(oauth2Login -> {
                oauth2Login.authorizationEndpoint(authConfig -> authConfig.authorizationRequestRepository(authorizationRequestRepository));

                /* We can use CustomSavedRequestAwareAuthenticationSuccessHandler which will forward the request to your original endpoint. */
                oauth2Login.successHandler(handler);

//                /* OR We can use this following handler which will not forward the request to your original endpoint rather return from here itself
//                   Here, we can return accessToken and refreshToken which we can get from authentication object passed in lambda context.*/
//                oauth2Login.successHandler((req, res, auth) -> {
//                        res.addHeader(HttpHeaders.SET_COOKIE, CookieHelper.generateExpiredCookie(OAUTH_COOKIE_NAME));
//                        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                        res.getWriter().write("{ \"status\": \"success\" }");
//                    }
//                );


            })
            .requestCache(reqConfig -> reqConfig.requestCache(requestCache))
            .addFilterBefore(setSecurityContextFilter(), AnonymousAuthenticationFilter.class )
            .oauth2Client(Customizer.withDefaults())
            .build();
    }

//    /**
//     * TODO : Comment out below config if dont want PKCE with secret clients.
//     * Reference video for the following config : https://www.youtube.com/watch?v=mKvi9RWGn3M
//     * Following config is for PKCE with secret client. If we dont want PKCE with secret client, we will have to comment the following config.
//     * OR we can comment this line resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
//     * OR we can change setting up of authoriationRequestCustomizer as resolver.setAuthorizationRequestCustomizer(customizer -> {}); which does nothing.
//     *
//     * Refer class and its fields {@link DefaultOAuth2AuthorizationRequestResolver},
//     * {@link DefaultOAuth2AuthorizationRequestResolver#authorizationRequestCustomizer},
//     * {@link DefaultOAuth2AuthorizationRequestResolver#DEFAULT_PKCE_APPLIER}
//     */
//    @Bean
//    public SecurityFilterChain configureFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
//
//        String uri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
//
//        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, uri);
//        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
//        return http.authorizeRequests()
//            .anyRequest().authenticated()
//            .and()
//            .oauth2Login(oauth2Login -> {
//                oauth2Login.authorizationEndpoint().authorizationRequestResolver(resolver);
//            })
//            .oauth2Client(Customizer.withDefaults())
//            .build();
//    }
}
