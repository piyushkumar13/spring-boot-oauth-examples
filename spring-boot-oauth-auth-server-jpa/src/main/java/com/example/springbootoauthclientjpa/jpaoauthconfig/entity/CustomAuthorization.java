/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * This calls aggregates fields of multiple predefined classes :
 * {@link org.springframework.security.oauth2.server.authorization.OAuth2Authorization}
 * {@link org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode}
 * {@link org.springframework.security.oauth2.core.OAuth2AccessToken}
 * {@link org.springframework.security.oauth2.core.oidc.OidcIdToken}
 * {@link org.springframework.security.oauth2.core.OAuth2RefreshToken}
 *
 * Also, refer predefined jdbc class which defines all the columns to be aggregated {@link org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService}
 *
 * @see org.springframework.security.oauth2.core.AbstractOAuth2Token
 *
 * @author Piyush Kumar.
 * @since 07/10/23.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_authorization")
public class CustomAuthorization {

    /* From class OAuth2Authorization.java */
    @Id
    private String id;

    @Column(name = "registered_client_id")
    private String registeredClientId;

    @Column(name = "principal_name")
    private String principalName;

    @Column(name = "authorization_grant_type")
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", columnDefinition="TEXT")
    private String authorizedScopes;

    @Column(name = "attributes", columnDefinition="TEXT")
    private String attributes;

    private String state; // Its actually from private Map<Class<? extends OAuth2Token>, OAuth2Authorization.Token<?>> tokens; in OAuth2Authorization


    /* From class org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode.java */

    @Column(name = "authorization_code_token_value", columnDefinition="TEXT")
    private String authorizationCodeTokenValue;

    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;

    @Column(name = "authorization_code_metadata", columnDefinition = "TEXT")
    private String authorizationCodeMetadata;


    /* From class org.springframework.security.oauth2.core.OAuth2AccessToken.java */

    @Column(name = "access_token_value", columnDefinition="TEXT")
    private String accessTokenValue;

    @Column(name = "access_token_value_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_value_expires_at")
    private Instant accessTokenExpiresAt;

    @Column(name = "access_token_type")
    private String accessTokenType;

    @Column(name = "access_token_scopes", columnDefinition="TEXT")
    private String accessTokenScopes;

    @Column(name = "access_token_metadata", columnDefinition = "TEXT")
    private String accessTokenMetadata;


    /* From class org.springframework.security.oauth2.core.oidc.OidcIdToken.java */

    @Column(name = "oidc_id_token_value", columnDefinition="TEXT")
    private String oidcIdTokenValue;

    @Column(name = "oidc_id_token_issued_at")
    private Instant oidcIdTokenIssuedAt;

    @Column(name = "oidc_id_token_expires_at")
    private Instant oidcIdTokenExpiresAt;

    @Column(name = "oidc_id_token_claims", columnDefinition = "TEXT")
    private String oidcIdTokenClaims;

    @Column(name = "oidc_id_metadata", columnDefinition = "TEXT")
    private String oidcIdMetadata;


    /* From class org.springframework.security.oauth2.core.OAuth2RefreshToken.java */

    @Column(name = "refresh_token_value", columnDefinition="TEXT")
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;

    @Column(name = "refresh_token_metadata", columnDefinition = "TEXT")
    private String refeshTokenMetadata;
}
