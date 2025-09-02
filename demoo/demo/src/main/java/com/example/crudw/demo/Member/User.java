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
@Entity
@Table(name = "users")
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

    public User() {

    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getRoleKey() {
        return role.getKey();
    }

    public void setRegistrationId(String registrationId){this.registrationId =registrationId;}

    public String getRole() {
        if (role == null) {
            return Role.DEFAULT.getKey();  // 기본값을 반환
        }
        return role.getKey();
    }
    public void setRole(Role role) {
        this.role = role;
    }


    @Builder
    public User(String name, String email, String id, Role role,String registrationId) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.role = (role == null) ? Role.DEFAULT : role;
        this.registrationId = registrationId;
    }

    public User update(String name) {
        this.name = name;
        this.id = id;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));  // 권한 반환
    }
    @Override
    public String getPassword() {
        return this.pw;  // 비밀번호 반환
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
