/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclients.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Piyush Kumar.
 * @since 02/10/23.
 */

@EnableWebSecurity(debug = true)
@Configuration
public class Configs {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
