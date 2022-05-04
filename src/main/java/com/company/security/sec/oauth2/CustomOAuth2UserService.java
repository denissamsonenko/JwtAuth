package com.company.security.sec.oauth2;

import com.company.security.exception.OAuth2AuthenticationProcessingException;
import com.company.security.model.AuthProvider;
import com.company.security.model.User;
import com.company.security.repository.UserRepository;
import com.company.security.sec.UserPrincipal;
import com.company.security.sec.oauth2.user.OAuth2UserInfo;
import com.company.security.sec.oauth2.user.OAuth2UserInfoFactory;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.AuthenticationException;
import java.util.Optional;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        if(StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        User user = null;

        if (userOptional.isPresent()) {
            user = userOptional.get();

            if (!user.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }

            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());

        return userRepository.save(existingUser);
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .provider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))
                .providerId(oAuth2UserInfo.getId())
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .build();

        return userRepository.save(user);
    }
}
