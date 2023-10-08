/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.repository;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.CustomAuthorization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Piyush Kumar.
 * @since 07/10/23.
 */
public interface CustomAuthorizationRepository extends JpaRepository<CustomAuthorization, String> {

    Optional<CustomAuthorization> findByState(final String state);

    Optional<CustomAuthorization> findByAuthorizationCodeTokenValue(final String tokenValue);

    Optional<CustomAuthorization> findByAccessTokenValue(final String tokenValue);

    Optional<CustomAuthorization> findByRefreshTokenValue(final String tokenValue);

    Optional<CustomAuthorization> findByStateOrAuthorizationCodeTokenValueOrAccessTokenValueOrRefreshTokenValue(
        final String state,
        final String authorizationCodeTokenValue,
        final String accessTokenValue,
        final String refreshTokenValue
    );
}
