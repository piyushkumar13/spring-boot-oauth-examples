/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Piyush Kumar.
 * @since 16/09/23.
 */

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {


    /**
     * Configuring WebSecurityConfigurerAdapter is deprecated.
     * <a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter">check this</a>
     */
//    @Configuration
//    public static class ConfigUsingWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
//
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//
////            http.csrf().disable();
////            http
////                .authorizeRequests()
////                .anyRequest().authenticated();
//
//            OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        }
//
////        @Bean
////        public PasswordEncoder passwordEncoder(){
////            return new BCryptPasswordEncoder();
////        }
//    }


    @Configuration
    public static class ConfigUsingSecurityFilterChain {


        @Bean
        public InMemoryUserDetailsManager userDetailsManager() {

            /* With user details object, implementatio of it is User object - with these you can either set authorities or roles. If you set both the one which
             * is set later will override the authorities arraylist with the latest one.
             *
             * */
//            UserDetails user1 = User
//                .withUsername("piyush")
//                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("piyush123"))
//                .roles("ADMIN")
////                .authorities("READ", "WRITE", "DELETE")
//                .build();
//
//            UserDetails user2 = User
//                .withUsername("sandeep")
//                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("sandeep123"))
//                .roles("USER")
////                .authorities("READ", "WRITE")
//                .build();

            /* Either you can do it below.*/

//            List<SimpleGrantedAuthority> user1SimpleGrantedAuthorities = List.of(
//                new SimpleGrantedAuthority("ROLE_ADMIN"),
//                new SimpleGrantedAuthority("READ"),
//                new SimpleGrantedAuthority("WRITE"),
//                new SimpleGrantedAuthority("DELETE")
//            );
//            UserDetails user1 = User
//                .withUsername("piyush")
//                .password(passwordEncoder().encode("piyush123"))
//                .authorities(user1SimpleGrantedAuthorities)
//                .build();
//
//            List<SimpleGrantedAuthority> user2SimpleGrantedAuthorities = List.of(
//                new SimpleGrantedAuthority("ROLE_USER"),
//                new SimpleGrantedAuthority("READ"),
//                new SimpleGrantedAuthority("WRITE")
//            );
//
//            UserDetails user2 = User
//                .withUsername("sandeep")
//                .password(passwordEncoder().encode("sandeep123"))
//                .authorities(user2SimpleGrantedAuthorities)
//                .build();


            /* OR below but both above and below are eventually setting authorities. */


            UserDetails user1 = User
                .withUsername("piyush")
                .password(passwordEncoder().encode("piyush123"))
                .authorities("ROLE_ADMIN", "READ", "WRITE", "DELETE")
                .build();

            UserDetails user2 = User
                .withUsername("sandeep")
                .password(passwordEncoder().encode("sandeep123"))
                .authorities("ROLE_USER", "READ", "WRITE")
                .build();

            return new InMemoryUserDetailsManager(user1, user2);
        }


        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain configureSpringSecurityFilterChain(HttpSecurity http) throws Exception {

            return http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin(Customizer.withDefaults())
                .build();
        }
    }
}
