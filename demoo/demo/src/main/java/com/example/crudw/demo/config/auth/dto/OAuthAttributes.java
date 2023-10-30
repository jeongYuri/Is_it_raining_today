package com.example.crudw.demo.config.auth.dto;

import com.example.crudw.demo.Member.Role;
import com.example.crudw.demo.Member.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String id;
    private String name;
    private String email;
    private String picture;
    private String registrationId;


    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String id,String nameAttributeKey, String name, String email, String picture,String registrationId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.id = id;
        this.email = email;
        this.picture = picture;
        this.registrationId = registrationId;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("naver".equals(registrationId)){
            return ofNaver("id",attributes,registrationId);
        }else if("kakao".equals(registrationId)){
            return ofKaKao("id",attributes,registrationId);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }
    private static OAuthAttributes ofKaKao(String userNameAttributeName, Map<String, Object> attributes,String registrationId) {
        Map<String,Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String,Object> account = (Map<String, Object>) attributes.get("profile");
        Map<String,Object> accounts = (Map<String, Object>) attributes.get("properties");

        return OAuthAttributes.builder()
                .name((String) accounts.get("nickname"))
                .email((String) response.get("email"))
                .registrationId(registrationId)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();

    }
    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes,String registrationId) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String id = (String) response.get("id");


        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .registrationId(registrationId)
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .id((String)attributes.get("id"))
                .picture((String) attributes.get("picture"))
                .registrationId("google")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .id(id)
                .role(Role.GUEST)
                .registrationId(registrationId)
                .build();
    }
}