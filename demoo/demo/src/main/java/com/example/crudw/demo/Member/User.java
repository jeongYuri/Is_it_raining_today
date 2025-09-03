package com.example.crudw.demo.Member;


import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends TimeEntity implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    private String id;
    private String pw;
    private String name;
    private String email;
    private String phone;
    private String registrationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.DEFAULT;

    @Builder
    public User(String name, String email, String id, Role role, String registrationId, String pw) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.role = (role == null) ? Role.DEFAULT : role;
        this.registrationId = registrationId;
        this.pw = pw;
    }

    public String getRoleKey() {
        return role.getKey();
    }

    // UserDetails 메서드들을 올바르게 구현합니다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.pw;
    }

    // Spring Security의 고유 ID(username)를 반환하도록 수정
    @Override
    public String getUsername() {
        return this.id;
    }

    // 계정 상태를 true로 설정하여 정상 로그인 가능하도록 수정
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
