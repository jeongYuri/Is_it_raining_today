package com.example.crudw.demo.config.auth;

import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserRepository;
import com.example.crudw.demo.Service.UserService;
import com.example.crudw.demo.config.auth.dto.OAuthAttributes;
import com.example.crudw.demo.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("socialUser", new SessionUser(user));
        System.out.println("엥"+new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user;
        Optional<User> userOptional = userRepository.findByEmail(attributes.getEmail());
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = attributes.toEntity();
            userRepository.save(user);
            userOptional = userRepository.findByEmail(attributes.getEmail());
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
        }
        return user;
    }
}
/*
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName()))
                .orElse(attributes.toEntity());
        System.out.println(user);
        /*
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()))));

        return userRepository.save(user);

    }


    /*User user;
        Optional<User> userOptional = userRepository.findByEmail(attributes.getEmail());
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = attributes.toEntity();
            userRepository.save(user);
            userOptional = userRepository.findByEmail(attributes.getEmail());
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
        }return user;
}*/

