package com.example.springbootoauthclientscustomization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * In order to make oauth2-client stateless, in order to avoid authorization_request_not_found error.
 *
 * We need to store the details like HttpServletRequest, OAuth2AuthorizationRequest, or anything in some shared storage like in cookies or some persistence storage.
 * With this idea, in case of oauth-client, we have the following approaches
 * * we can store requests in cookie by implementing AuthorizationRequestRepository and use CookieRequestCache(in-built class) implementation of RequestCache.
 * * We can store HttpSession in database.
 * * We can use some other identifier to fetch requests which we want to store like we can use state id which is sent by authorisation-server in uri. We can store this id along with request - like a key value pair. And we can then retrieve using the request using state id.
 *
 * NOTE : Make sure when you call the apis in SimpleRestController use http://127.0.0.1 since that is configured in auth-server as redirect url, otherwise cookies will not be
 * picked up as cookie picked up by exact string match with the domain name, therefore, though 127.0.0.1 and localhost both means same host but string is different.
 * */

@SpringBootApplication
public class SpringBootOauthClientsCustomizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOauthClientsCustomizationApplication.class, args);
    }

}
