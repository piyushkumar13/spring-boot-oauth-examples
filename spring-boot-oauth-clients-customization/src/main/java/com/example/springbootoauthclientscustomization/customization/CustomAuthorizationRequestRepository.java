package com.example.springbootoauthclientscustomization.customization;

import static com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper.OAUTH_COOKIE_EXPIRY;

import com.example.springbootoauthclientscustomization.customization.helpers.CookieHelper;
import com.example.springbootoauthclientscustomization.customization.helpers.EncryptionHelper;
import java.time.Duration;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH_COOKIE_NAME = "OAUTH";
    public static final String AUTHENTICATION_OBJ = "MY_AUTHENTICATION_OBJ";


//    private final SecretKey encryptionKey;

//    public CustomAuthorizationRequestRepository() {
//        this.encryptionKey = EncryptionHelper.generateKey();
//    }

//    public CustomAuthorizationRequestRepository(@NonNull char[] encryptionPassword) {
//        byte[] salt = {0}; // A static salt is OK for these short lived session cookies
//        this.encryptionKey = EncryptionHelper.generateKey(encryptionPassword, salt);
//    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return this.retrieveCookie(request);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            this.removeCookie(response);
            return;
        }
        this.attachCookie(response, authorizationRequest);
    }

    @Override
    @Deprecated
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.retrieveCookie(request);
    }

    private OAuth2AuthorizationRequest retrieveCookie(HttpServletRequest request) {
        return CookieHelper.retrieve(request.getCookies(), OAUTH_COOKIE_NAME)
            .map(EncryptionHelper::decrypt)
            .filter(OAuth2AuthorizationRequest.class::isInstance)
            .map(OAuth2AuthorizationRequest.class::cast)
            .orElse(null);
    }

    private void attachCookie(HttpServletResponse response, OAuth2AuthorizationRequest value) {
        String cookie = CookieHelper.generateCookie(OAUTH_COOKIE_NAME, EncryptionHelper.encrypt(value), OAUTH_COOKIE_EXPIRY);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie);
    }

    private void removeCookie(HttpServletResponse response) {
        String expiredCookie = CookieHelper.generateExpiredCookie(OAUTH_COOKIE_NAME);
        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie);
    }
}
