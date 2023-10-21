package com.example.springbootoauthclientscustomization.customization.helpers;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.util.Optional;
import javax.servlet.http.Cookie;
import lombok.NonNull;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;

public class CookieHelper {

    public static final Duration OAUTH_COOKIE_EXPIRY = Duration.ofMinutes(5);

    public static final String COOKIE_DOMAIN = "127.0.0.1";
    public static final Boolean HTTP_ONLY = Boolean.TRUE;
    public static final Boolean SECURE = Boolean.FALSE;

    public static Optional<String> retrieve(Cookie[] cookies, @NonNull String name) {
        if (isNull(cookies)) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public static String generateCookie(@NonNull String name, @NonNull String value, @NonNull Duration maxAge) {
        // Build cookie instance
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setHttpOnly(HTTP_ONLY);
        cookie.setSecure(SECURE);
        cookie.setMaxAge((int) maxAge.toSeconds());
        cookie.setPath("/");
        // Generate cookie string
        Rfc6265CookieProcessor processor = new Rfc6265CookieProcessor();
        return processor.generateHeader(cookie);
    }

    public static String generateExpiredCookie(@NonNull String name) {
        return generateCookie(name, "-", Duration.ZERO);
    }

}
