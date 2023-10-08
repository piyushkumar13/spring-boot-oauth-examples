/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is inspired from {@link org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent}.
 * Also, refer predefined jdbc class which defines all the columns to be aggregated {@link org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService}
 *
 * @author Piyush Kumar.
 * @since 07/10/23.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_authorization_consent")
@IdClass(CustomAuthorizationConsent.AuthConsentId.class)
public class CustomAuthorizationConsent {

    @Id
    @Column(name = "registered_client_id")
    private String registeredClientId;

    @Id
    @Column(name = "principal_name")
    private String principalName;

    @Column(name = "authorities")
    private String authorities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthConsentId implements Serializable {

        private String registeredClientId;
        private String principalName;

    }
}


