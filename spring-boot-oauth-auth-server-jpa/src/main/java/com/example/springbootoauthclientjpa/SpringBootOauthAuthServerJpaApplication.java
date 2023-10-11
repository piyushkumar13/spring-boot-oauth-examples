package com.example.springbootoauthclientjpa;

import static java.util.Objects.nonNull;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.CustomRegisteredClient;
import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.User;
import com.example.springbootoauthclientjpa.jpaoauthconfig.repository.CustomRegisteredClientRepository;
import com.example.springbootoauthclientjpa.jpaoauthconfig.repository.UserRepository;
import com.example.springbootoauthclientjpa.jpaoauthconfig.service.CustomAuthorizationService;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

@Data
@Slf4j
@SpringBootApplication
public class SpringBootOauthAuthServerJpaApplication implements ApplicationRunner {

    private final CustomRegisteredClientRepository customRegisteredClientRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOauthAuthServerJpaApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        log.info(":::: Initiating persisting CustomRegisteredClient :::: ");
        String id = UUID.randomUUID().toString();

        /*
        We can use ClientSettings method to configure
        requireProofKey - required for enabling PKCE,
        requireAuthorizationConsent
        */
        ClientSettings clientSettings = ClientSettings.builder()
            .requireProofKey(false)
            .requireAuthorizationConsent(false)
            .build();

        /*
        We can use TokenSettings method to configure
        authorizationCodeTimeToLive,
        accessTokenTimeToLive,
        refreshTokenTimeToLive,
        accessTokenFormat,
        reuseRefreshTokens,
        idTokenSignatureAlgorithm
        */
        TokenSettings tokenSettings = TokenSettings.builder().build();


        CustomRegisteredClient customRegisteredClient = CustomRegisteredClient.builder()
            .id(id)
            .clientId("client1")
            .clientName(id)
            .clientSecret(passwordEncoder.encode("myclientsecret"))
            .authorizationGrantTypes(AuthorizationGrantType.AUTHORIZATION_CODE.getValue() + "," + AuthorizationGrantType.REFRESH_TOKEN.getValue())
            .redirectUris("http://127.0.0.1:8080/login/oauth2/code/my-client-oidc")
//            .redirectUris("http://127.0.0.1:8080/authorized")
            .scopes(OidcScopes.OPENID + "," + "read")
            .clientAuthenticationMethods(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue())
            .clientSettings(writeMap(clientSettings.getSettings()))
            .tokenSettings(writeMap(tokenSettings.getSettings()))
            .clientIdIssuedAt(Instant.now())
            .clientSecretExpiresAt(Instant.now().plus(365, ChronoUnit.DAYS))
            .build();

        if (customRegisteredClientRepository.findByClientId("client1").isEmpty()) {
            log.info("Saving client as it is not already present, clientId=client1");
            customRegisteredClientRepository.save(customRegisteredClient);
        }

        log.info(":::: Persisting CustomRegisteredClient done :::: ");

        log.info(":::: Initiating persisting User :::: ");

        User user1 = User.builder()
            .firstName("Piyush")
            .lastName("Kumar")
            .emailId("pk@pk.com")
            .password(passwordEncoder.encode("piyush123"))
            .country("IN")
            .roles("ROLE_ADMIN")
            .authorities("READ,WRITE,DELETE")
            .isActive(true)
            .build();

        User user1ByEmailId = userRepository.getUserByEmailId("pk@pk.com");
        if (Objects.isNull(user1ByEmailId) || Objects.isNull(user1ByEmailId.getEmailId())){
            log.info("Saving user1 as it is not already present, emailId=pk@pk.com");
            userRepository.save(user1);
        }

        User user2 = User.builder()
            .firstName("Sandeep")
            .lastName("Kumar")
            .emailId("sandeep@sandeep.com")
            .password(passwordEncoder.encode("sandeep123"))
            .country("IN")
            .roles("ROLE_USER")
            .authorities("READ,WRITE")
            .isActive(true)
            .build();

        User user2ByEmailId = userRepository.getUserByEmailId("sandeep@sandeep.com");
        if (Objects.isNull(user2ByEmailId) || Objects.isNull(user2ByEmailId.getEmailId())){
            log.info("Saving user2 as it is not already present, emailId=sandeep@sandeep.com");
            userRepository.save(user2);
        }

        log.info(":::: Persisting User done :::: ");
    }

    private String writeMap(Map<String, Object> data) {

        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = SpringBootOauthAuthServerJpaApplication.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());


        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}