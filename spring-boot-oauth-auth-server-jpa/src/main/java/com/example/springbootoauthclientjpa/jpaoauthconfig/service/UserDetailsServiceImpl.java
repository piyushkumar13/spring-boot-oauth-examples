/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.service;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.User;
import com.example.springbootoauthclientjpa.jpaoauthconfig.repository.UserRepository;
import java.util.Collection;
import java.util.HashSet;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Piyush Kumar.
 * @since 16/09/23.
 */
@Service
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {

        User user = userRepository.getUserByEmailId(emailId);

        if (!user.getEmailId().equals(emailId)) {
            throw new UsernameNotFoundException("Access Denied");
        }
        Collection<GrantedAuthority> authoriies = new HashSet<>();

        user.getAuthoritiesList().forEach(auth -> authoriies.add(new SimpleGrantedAuthority(auth)));

        return new org.springframework.security.core.userdetails.User(
            user.getEmailId(),
            user.getPassword(),
            user.isActive(),
            true,
            true,
            true,
            authoriies
        );
    }
}
