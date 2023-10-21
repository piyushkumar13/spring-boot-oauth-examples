/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientscustomization.customization;

import static com.example.springbootoauthclientscustomization.customization.CustomAuthorizationRequestRepository.AUTHENTICATION_OBJ;
import static com.example.springbootoauthclientscustomization.customization.CustomAuthorizationRequestRepository.OAUTH_COOKIE_NAME;
import static com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper.OAUTH_COOKIE_EXPIRY;

import com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper;
import com.example.springbootoauthclientscustomization.customization.helpers.EncryptionHelper;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * @author Piyush Kumar.
 * @since 21/10/23.
 */

@Component
public class CustomSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication auth) throws ServletException, IOException {

        if (!(auth instanceof OAuth2AuthenticationToken)) {
            System.out.println("It is not OAuth2AuthenticationToken");
            return;
        }

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) auth;
        AuthenticationDataToStore authenticationDataToStore = map(authToken);

        res.addHeader(HttpHeaders.SET_COOKIE, CookieHelper.generateExpiredCookie(OAUTH_COOKIE_NAME));

        String cookie = CookieHelper.generateCookie(AUTHENTICATION_OBJ, EncryptionHelper.encrypt(authenticationDataToStore), OAUTH_COOKIE_EXPIRY);
        res.addHeader(HttpHeaders.SET_COOKIE, cookie);

        super.onAuthenticationSuccess(req, res, auth);
    }

    public AuthenticationDataToStore map(OAuth2AuthenticationToken authToken) {

        OAuth2User principal = authToken.getPrincipal();
        OidcUser oidcUser = (OidcUser) principal;

        String idToken = oidcUser.getIdToken().getTokenValue();
        Map<String, Object> claims = oidcUser.getClaims();
        Instant issuedAt = oidcUser.getIssuedAt();
        Instant expiresAt = oidcUser.getExpiresAt();

        return AuthenticationDataToStore.builder()
            .idToken(idToken)
            .claims(claims)
            .authorities(getAuthorities(principal.getAuthorities()))
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .authorizedClientRegistrationId("my-client-oidc")
            .build();
    }

    private Collection<String> getAuthorities(Collection<? extends GrantedAuthority> authorities) {

        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthenticationDataToStore implements Serializable {

        private String idToken;
        private String userInfo;

        private String authorizedClientRegistrationId;

        private Collection<String> authorities;

        private Map<String, Object> claims;

        private Instant issuedAt;

        private Instant expiresAt;
    }
}
