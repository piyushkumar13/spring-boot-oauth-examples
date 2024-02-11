///*
// *  Copyright (c) 2023 DMG
// *  All Rights Reserved Worldwide.
// *
// *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
// *  AND CONSTITUTES A VALUABLE TRADE SECRET.
// */
//
//package com.example.springbootoauthclients.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//
///**
// * @author Piyush Kumar.
// * @since 11/02/24.
// */
//
///**
// * NOTE TODO : This configuration is required only for logging out for okta. So, use this configuration when application yaml is having only okta configuration.
// */
//@EnableWebSecurity
//public class SecurityConfigForOktaLogout extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private ClientRegistrationRepository clientRegistrationRepository;
//
////
////    /**
////     * Following configuration will logout the endpoint, clear the cookies and session but session with okta will not be cleared. Means okta is still logged in.
////     * If you try to hit the endpoint point again which requires okta login(i.e typing in the okta username and password) will not be required as okta session is still alive.
////     */
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////
////        http.authorizeRequests()
////            .antMatchers("/hello").permitAll()
////            .anyRequest().authenticated()
////            .and()
////            .oauth2Login()
////            .and()
////            .logout()
////            .logoutSuccessUrl("/hello")
////            .invalidateHttpSession(true)
////            .clearAuthentication(true)
////            .deleteCookies("JSESSIONID");
////    }
//
//
//    /**
//     * Following configuration will logout the endpoint, clear the cookies and session and session with okta will also be logged out.
//     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http.authorizeRequests()
//            .antMatchers("/hello").permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .oauth2Login()
//            .and()
//            .logout()
//            .logoutSuccessHandler(oidcClientInitiatedLogoutSuccessHandler())
//            .invalidateHttpSession(true)
//            .clearAuthentication(true)
//            .deleteCookies("JSESSIONID");
//    }
//
//    private OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler() {
//        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
//
//        handler.setPostLogoutRedirectUri("http://127.0.0.1:8080/hello");
//
//        return handler;
//    }
//}
