/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.service;

import static java.util.Objects.nonNull;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.CustomRegisteredClient;
import com.example.springbootoauthclientjpa.jpaoauthconfig.repository.CustomRegisteredClientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.log.Log;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Class is inspired from {@link org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository}.
 *
 * @author Piyush Kumar.
 * @since 07/10/23.
 */
@Slf4j
@Data
@Service
public class CustomRegisteredClientRepositoryService implements RegisteredClientRepository {

    private CustomRegisteredClientRepository customRegisteredClientRepository;

    private ObjectMapper objectMapper;

    public CustomRegisteredClientRepositoryService(CustomRegisteredClientRepository customRegisteredClientRepository){

        this.customRegisteredClientRepository = customRegisteredClientRepository;

        this.objectMapper = new ObjectMapper();

        ClassLoader classLoader = CustomRegisteredClientRepositoryService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }



    @Override
    public void save(final RegisteredClient registeredClient) {

        if (Objects.isNull(registeredClient)){
            log.error("Registered client is null to be saved. Skipping save.");
            return;
        }

        customRegisteredClientRepository.save(map(registeredClient));
    }

    @Override
    public RegisteredClient findById(final String id) {

        return map(customRegisteredClientRepository.findById(id).orElse(null));
    }

    @Override
    public RegisteredClient findByClientId(final String clientId) {

        return map(customRegisteredClientRepository.findByClientId(clientId).orElse(null));
    }

    private CustomRegisteredClient map(final RegisteredClient registeredClient){

        List<String> clientAuthenticationMethods = new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
            clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
            authorizationGrantTypes.add(authorizationGrantType.getValue()));

        return CustomRegisteredClient.builder()
            .id(registeredClient.getId())
            .clientId(registeredClient.getClientId())
            .clientIdIssuedAt(registeredClient.getClientIdIssuedAt())
            .clientSecret(registeredClient.getClientSecret())
            .clientSecretExpiresAt(registeredClient.getClientSecretExpiresAt())
            .clientName(registeredClient.getClientName())
            .clientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods))
            .authorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes))
            .redirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()))
            .scopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()))
            .clientSettings(writeMap(registeredClient.getClientSettings().getSettings()))
            .clientSettings(writeMap(registeredClient.getTokenSettings().getSettings()))
            .build();
    }

    private RegisteredClient map(final CustomRegisteredClient customRegisteredClient){

        if (Objects.isNull(customRegisteredClient)){
            return null;
        }

        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(customRegisteredClient.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(customRegisteredClient.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(customRegisteredClient.getRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(customRegisteredClient.getScopes());


        RegisteredClient.Builder builder = RegisteredClient.withId(customRegisteredClient.getId());

        builder
            .clientId(customRegisteredClient.getClientId())
            .clientIdIssuedAt(customRegisteredClient.getClientIdIssuedAt())
            .clientSecret(customRegisteredClient.getClientSecret())
            .clientSecretExpiresAt(customRegisteredClient.getClientSecretExpiresAt())
            .clientName(customRegisteredClient.getClientName())

            .clientAuthenticationMethods((authenticationMethods) ->
                clientAuthenticationMethods.forEach(authenticationMethod ->
                    authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))

            .authorizationGrantTypes((grantTypes) ->
                authorizationGrantTypes.forEach(grantType ->
                    grantTypes.add(resolveAuthorizationGrantType(grantType))))

            .redirectUris((uris) -> uris.addAll(redirectUris))
            .scopes((scopes) -> scopes.addAll(clientScopes));

        Map<String, Object> clientSettingsMap = parseMap(customRegisteredClient.getClientSettings());
        builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());

        Map<String, Object> tokenSettingsMap = parseMap(customRegisteredClient.getTokenSettings());

//        long accessTimeToLive = ((Double) tokenSettingsMap.get("settings.token.access-token-time-to-live")).longValue();
//        tokenSettingsMap.put("settings.token.access-token-time-to-live", Duration.ofSeconds(accessTimeToLive));
//
//        long refreshTimeToLive = ((Double) tokenSettingsMap.get("settings.token.refresh-token-time-to-live")).longValue();
//        tokenSettingsMap.put("settings.token.refresh-token-time-to-live", Duration.ofSeconds(refreshTimeToLive));
//
//        long authorizationCodeTimeToLive = ((Double) tokenSettingsMap.get("settings.token.authorization-code-time-to-live")).longValue();
//        tokenSettingsMap.put("settings.token.authorization-code-time-to-live", Duration.ofSeconds(authorizationCodeTimeToLive));

        TokenSettings.Builder tokenSettingsBuilder = TokenSettings.withSettings(tokenSettingsMap);
        if (!tokenSettingsMap.containsKey(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT)) {
            tokenSettingsBuilder.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED);
        }
        builder.tokenSettings(tokenSettingsBuilder.build());

        return builder.build();

    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(final String clientAuthenticationMethod) {

        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);		// Custom client authentication method
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(final String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(authorizationGrantType);		// Custom authorization grant type
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
