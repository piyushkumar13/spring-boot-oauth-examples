/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientscustomization.customization;

import static com.example.springbootoauthclientscustomization.customization.CustomAuthorizationRequestRepository.AUTHENTICATION_OBJ;

import com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper;
import com.example.springbootoauthclientscustomization.customization.helpers.EncryptionHelper;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Piyush Kumar.
 * @since 21/10/23.
 */
public class SetSecurityContextFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /* TODO : Here we can also validate the cookie expiry and access token expiry so that we wont set the security context and flow will
        *  TODO : then go to AnonymousAuthenticationFilter which will create a new empty context and then goes to eventually FilterSecurityFilter which
        *  TODO : will throw the access denied exception and will raise error and along with configuring, failure handler to route to login page or any endpoint which we
        *  TODO : would like to configure in order to re-allow login, we can again fetch access token.*/
        CustomSavedRequestAwareAuthenticationSuccessHandler.AuthenticationDataToStore authenticationData = CookieHelper.retrieve(request.getCookies(), AUTHENTICATION_OBJ)
            .map(EncryptionHelper::decrypt)
            .filter(CustomSavedRequestAwareAuthenticationSuccessHandler.AuthenticationDataToStore.class::isInstance)
            .map(CustomSavedRequestAwareAuthenticationSuccessHandler.AuthenticationDataToStore.class::cast)
            .orElse(null);

        if (authenticationData == null){
            // just for debugging.
            System.out.println("OauthToken Is null");
        }

        if (authenticationData != null) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createAuthentication(authenticationData));
            SecurityContextHolder.setContext(context);

            System.out.println("OauthToken Is present");
        }

        filterChain.doFilter(request, response);
    }

    private OAuth2AuthenticationToken createAuthentication(CustomSavedRequestAwareAuthenticationSuccessHandler.AuthenticationDataToStore authenticationData){

        String idToken = authenticationData.getIdToken();
        Map<String, Object> claims = authenticationData.getClaims();
        Instant issuedAt = authenticationData.getIssuedAt();
        Instant expiresAt = authenticationData.getExpiresAt();

        OidcIdToken token = new OidcIdToken(idToken, issuedAt, expiresAt, claims);

        Collection<String> authorities = authenticationData.getAuthorities();
        Collection<? extends GrantedAuthority> grantedAuthorities = getAuthorities(authorities);
        OAuth2User oAuth2User = new DefaultOidcUser(grantedAuthorities, token);

        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oAuth2User, grantedAuthorities, authenticationData.getAuthorizedClientRegistrationId());

        return authenticationToken;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<String> authorities){
         return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
