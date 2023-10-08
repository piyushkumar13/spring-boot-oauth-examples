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
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 *
 * This class is referred from class {@link org.springframework.security.oauth2.server.authorization.client.RegisteredClient}
 *
 * @author Piyush Kumar.
 * @since 07/10/23.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Oauth2_registered_client")
public class CustomRegisteredClient {

    @Id
    private String id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_id_issuedAt")
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_authentication_methods")
    private String clientAuthenticationMethods; // In spring RegisteredClient, it is set.

    @Column(name = "authorization_grant_types")
    private String authorizationGrantTypes; // In spring RegisteredClient, it is set.

    @Column(name = "redirect_uris", columnDefinition = "TEXT")
    private String redirectUris; // In spring RegisteredClient, it is set.

    @Column(name = "scopes")
    private String scopes; // In spring RegisteredClient, it is set.

    @Column(name = "client_settings", length = 2000)
    private String clientSettings;

    @Column(name = "token_settings", length = 2000)
    private String tokenSettings;
}

