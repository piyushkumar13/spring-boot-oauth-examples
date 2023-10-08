/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.service;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.CustomAuthorizationConsent;
import com.example.springbootoauthclientjpa.jpaoauthconfig.repository.CustomAuthorizationConsentRepository;
import java.sql.Types;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Also, refer class {@link org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService} which is one of the implementation of {@link OAuth2AuthorizationConsentService}.
 * Following class implementation is inspired from class {@link org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService}.
 *
 * @author Piyush Kumar.
 * @since 07/10/23.
 */

@Data
@Slf4j
@Service
public class CustomAuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final CustomAuthorizationConsentRepository customAuthorizationConsentRepository;
    private final RegisteredClientRepository registeredClientRepository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {

        if (Objects.isNull(authorizationConsent)){
            log.error("Authorization Consent is null. Skipping saving of Authorization Consent.");
            return;
        }

        customAuthorizationConsentRepository.save(map(authorizationConsent));
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {

        if (Objects.isNull(authorizationConsent)){
            log.error("Authorization Consent is null. Skipping deletion of Authorization Consent.");
            return;
        }

        customAuthorizationConsentRepository.delete(map(authorizationConsent));
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {

        return map(customAuthorizationConsentRepository.findById(CustomAuthorizationConsent.AuthConsentId.builder().registeredClientId(registeredClientId).principalName(principalName).build()).orElse(null));
    }

    private CustomAuthorizationConsent map(final OAuth2AuthorizationConsent authorizationConsent) {

        CustomAuthorizationConsent.CustomAuthorizationConsentBuilder customAuthorizationConsentBuilder = CustomAuthorizationConsent.builder()
            .registeredClientId(authorizationConsent.getRegisteredClientId())
            .principalName(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        String authoritiesStr = StringUtils.collectionToDelimitedString(authorities, ",");

        customAuthorizationConsentBuilder.authorities(authoritiesStr);

        return customAuthorizationConsentBuilder.build();
    }

    private OAuth2AuthorizationConsent map(final CustomAuthorizationConsent customAuthorizationConsent) {

        if (Objects.isNull(customAuthorizationConsent)){
            return null;
        }

        String registeredClientId = customAuthorizationConsent.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent
            .withId(registeredClientId, customAuthorizationConsent.getPrincipalName());

        String authorizationConsentAuthorities = customAuthorizationConsent.getAuthorities();
        if (authorizationConsentAuthorities != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(authorizationConsentAuthorities)) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }

        return builder.build();
    }
}
