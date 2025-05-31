package org.example.digijed.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.digijed.models.AuthProvider;
import org.example.digijed.models.RoleType;
import org.example.digijed.models.User;
import org.example.digijed.repositories.UserRepository;
import org.example.digijed.security.CustomOAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("Loading OAuth2 user from provider: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());

        String providerId = oAuth2User.getAttribute("sub");

        log.debug("Looking up user with provider: {} and providerId: {}", provider, providerId);
        User user = userRepository.findByAuthProviderAndProviderId(authProvider, providerId)
                .orElseGet(() -> {
                    log.info("Creating new user for OAuth2 login with email: {}", Optional.ofNullable(oAuth2User.getAttribute("email")));
                    User newUser = new User();
                    newUser.setAuthProvider(authProvider);
                    newUser.setProviderId(providerId);
                    newUser.setUsername(oAuth2User.getAttribute("email"));
                    newUser.setEmail(oAuth2User.getAttribute("email"));
                    newUser.setPassword(null);
                    newUser.setRole(RoleType.USER);
                    newUser.setIsEnabled(true);

                    return userRepository.save(newUser);
                });

        log.debug("Successfully loaded OAuth2 user: {}", user.getUsername());
        return new CustomOAuth2User(oAuth2User, user);
    }
}
