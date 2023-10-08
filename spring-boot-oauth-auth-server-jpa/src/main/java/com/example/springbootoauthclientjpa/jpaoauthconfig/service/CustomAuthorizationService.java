/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.service;

import static java.util.Objects.nonNull;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.CustomAuthorization;
import com.example.springbootoauthclientjpa.jpaoauthconfig.repository.CustomAuthorizationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Also, refer class {@link org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService} which is one of the implementation of {@link OAuth2AuthorizationService}.
 * Following class implementation is inspired from class {@link org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService}.
 *
 * @author Piyush Kumar.
 * @since 07/10/23.
 */

@Data
@Slf4j
@Service
public class CustomAuthorizationService implements OAuth2AuthorizationService {

    private CustomAuthorizationRepository customAuthorizationRepository;
    private RegisteredClientRepository registeredClientRepository;
    private ObjectMapper objectMapper;

    public CustomAuthorizationService(CustomAuthorizationRepository customAuthorizationRepository, RegisteredClientRepository registeredClientRepository){

        this.customAuthorizationRepository = customAuthorizationRepository;
        this.registeredClientRepository = registeredClientRepository;

        this.objectMapper = new ObjectMapper();
        ClassLoader classLoader = CustomAuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(OAuth2Authorization authorization) {

        if (Objects.isNull(authorization)) {
            log.error("OAuth2Authorization is null. Skipping saving of OAuth2Authorization.");
            return;
        }

        customAuthorizationRepository.save(map(authorization));
    }

    @Override
    public void remove(OAuth2Authorization authorization) {

        if (Objects.isNull(authorization)) {
            log.error("OAuth2Authorization is null. Skipping removing of OAuth2Authorization.");
            return;
        }

        customAuthorizationRepository.delete(map(authorization));

    }

    @Override
    public OAuth2Authorization findById(String id) {

        return map(customAuthorizationRepository.findById(id).orElse(null));
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {

        if (tokenType == null) {

            return map(customAuthorizationRepository.findByStateOrAuthorizationCodeTokenValueOrAccessTokenValueOrRefreshTokenValue(token, token, token, token).orElse(null));

        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {

            return map(customAuthorizationRepository.findByState(token).orElse(null));

        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {

            return map(customAuthorizationRepository.findByAuthorizationCodeTokenValue(token).orElse(null));

        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {

            return map(customAuthorizationRepository.findByAccessTokenValue(token).orElse(null));

        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {

            return map(customAuthorizationRepository.findByRefreshTokenValue(token).orElse(null));

        }

        return null;
    }

    private CustomAuthorization map(final OAuth2Authorization authorization) {

        String authorizedScopes = null;
        if (!CollectionUtils.isEmpty(authorization.getAuthorizedScopes())) {
            authorizedScopes = StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ",");
        }

        String attributes = writeMap(authorization.getAttributes());

        String state = null;
        String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(authorizationState)) {
            state = authorizationState;
        }

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();

        return CustomAuthorization.builder()
            .id(authorization.getId())
            .registeredClientId(authorization.getRegisteredClientId())
            .principalName(authorization.getPrincipalName())
            .authorizationGrantType(authorization.getAuthorizationGrantType().getValue())
            .authorizedScopes(authorizedScopes)
            .attributes(attributes)
            .state(state)
            .authorizationCodeTokenValue((nonNull(authorizationCode) && nonNull(authorizationCode.getToken())) ? authorizationCode.getToken().getTokenValue() : null)
            .authorizationCodeIssuedAt((nonNull(authorizationCode) && nonNull(authorizationCode.getToken())) ? authorizationCode.getToken().getIssuedAt() : null)
            .authorizationCodeExpiresAt((nonNull(authorizationCode) && nonNull(authorizationCode.getToken())) ? authorizationCode.getToken().getExpiresAt() : null)
            .authorizationCodeMetadata((nonNull(authorizationCode) && nonNull(authorizationCode.getToken())) ? writeMap(authorizationCode.getMetadata()) : null)
            .accessTokenValue((nonNull(accessToken) && nonNull(accessToken.getToken())) ? accessToken.getToken().getTokenValue() : null)
            .accessTokenIssuedAt((nonNull(accessToken) && nonNull(accessToken.getToken())) ? accessToken.getToken().getIssuedAt() : null)
            .accessTokenExpiresAt((nonNull(accessToken) && nonNull(accessToken.getToken())) ? accessToken.getToken().getExpiresAt() : null)
            .accessTokenMetadata((nonNull(accessToken) && nonNull(accessToken.getToken())) ? writeMap(accessToken.getMetadata()) : null)
            .accessTokenScopes((nonNull(accessToken) && nonNull(accessToken.getToken())) ? StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ",") : null)
            .accessTokenType((nonNull(accessToken) && nonNull(accessToken.getToken()) && nonNull(accessToken.getToken().getTokenType())) ? accessToken.getToken().getTokenType().getValue() : null)
            .oidcIdTokenValue((nonNull(oidcIdToken) && nonNull(oidcIdToken.getToken())) ? oidcIdToken.getToken().getTokenValue() : null)
            .oidcIdTokenIssuedAt((nonNull(oidcIdToken) && nonNull(oidcIdToken.getToken())) ? oidcIdToken.getToken().getIssuedAt() : null)
            .oidcIdTokenExpiresAt((nonNull(oidcIdToken) && nonNull(oidcIdToken.getToken())) ? oidcIdToken.getToken().getExpiresAt() : null)
            .oidcIdMetadata((nonNull(oidcIdToken) && nonNull(oidcIdToken.getToken())) ? writeMap(oidcIdToken.getMetadata()) : null)
            .refreshTokenValue((nonNull(refreshToken) && nonNull(refreshToken.getToken())) ? refreshToken.getToken().getTokenValue() : null)
            .refreshTokenIssuedAt((nonNull(refreshToken) && nonNull(refreshToken.getToken())) ? refreshToken.getToken().getIssuedAt() : null)
            .refreshTokenExpiresAt((nonNull(refreshToken) && nonNull(refreshToken.getToken())) ? refreshToken.getToken().getExpiresAt() : null)
            .refeshTokenMetadata((nonNull(refreshToken) && nonNull(refreshToken.getToken())) ? writeMap(refreshToken.getMetadata()) : null)
            .build();

    }

    private OAuth2Authorization map(final CustomAuthorization customAuthorization) {

        if (Objects.isNull(customAuthorization)) {

            return null;
        }

        RegisteredClient registeredClient = this.registeredClientRepository.findById(customAuthorization.getRegisteredClientId());

        if (registeredClient == null) {
            log.error("The registeredClient was not found in the repository, id={}", customAuthorization.getRegisteredClientId());
            throw new DataRetrievalFailureException(
                "The RegisteredClient with id '" + customAuthorization.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);

        builder
            .id(customAuthorization.getId())
            .principalName(customAuthorization.getPrincipalName())
            .authorizationGrantType(new AuthorizationGrantType(customAuthorization.getAuthorizationGrantType()))
            .authorizedScopes(StringUtils.commaDelimitedListToSet(customAuthorization.getAuthorizedScopes()))
            .attributes((attrs) -> attrs.putAll(parseMap(customAuthorization.getAttributes())));

        if (StringUtils.hasText(customAuthorization.getAuthorizationCodeTokenValue())) {

            Map<String, Object> authorizationCodeMetadata = parseMap(customAuthorization.getAuthorizationCodeMetadata());
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(customAuthorization.getAuthorizationCodeTokenValue(), customAuthorization.getAuthorizationCodeIssuedAt(), customAuthorization.getAuthorizationCodeExpiresAt());
            builder.token(authorizationCode, (metadata) -> metadata.putAll(authorizationCodeMetadata));

        }

        if (StringUtils.hasText( customAuthorization.getAccessTokenValue())) {

            OAuth2AccessToken.TokenType tokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(customAuthorization.getAccessTokenType())) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            }
            Map<String, Object> accessTokenMetadata = parseMap(customAuthorization.getAccessTokenMetadata());
            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, customAuthorization.getAccessTokenValue(), customAuthorization.getAccessTokenIssuedAt(), customAuthorization.getAccessTokenExpiresAt(),
                StringUtils.commaDelimitedListToSet(customAuthorization.getAccessTokenScopes()));

            builder.token(accessToken, (metadata) -> metadata.putAll(accessTokenMetadata));

        }

        if (StringUtils.hasText(customAuthorization.getOidcIdTokenValue())) {

            Map<String, Object> oidcTokenMetadata = parseMap(customAuthorization.getOidcIdMetadata());
            OidcIdToken oidcToken = new OidcIdToken(customAuthorization.getOidcIdTokenValue(), customAuthorization.getOidcIdTokenIssuedAt(), customAuthorization.getOidcIdTokenExpiresAt(),
                (Map<String, Object>) oidcTokenMetadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME));

            builder.token(oidcToken, (metadata) -> metadata.putAll(oidcTokenMetadata));
        }

        if (StringUtils.hasText(customAuthorization.getRefreshTokenValue())) {
            Map<String, Object> refreshTokenMetadata = parseMap(customAuthorization.getRefeshTokenMetadata());
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(customAuthorization.getRefreshTokenValue(), customAuthorization.getRefreshTokenIssuedAt(), customAuthorization.getRefreshTokenExpiresAt());
            builder.token(refreshToken, (metadata) -> metadata.putAll(refreshTokenMetadata));
        }

        if (StringUtils.hasText(customAuthorization.getState())) {
            builder.attribute(OAuth2ParameterNames.STATE, customAuthorization.getState());
        }


        return builder.build();
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private Map<String, Object> parseMap(String data) {

        if (data == null){
            return null;
        }

        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
