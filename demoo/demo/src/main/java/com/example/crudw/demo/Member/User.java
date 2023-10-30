package com.example.crudw.demo.Member;


import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "users")
public class User extends TimeEntity {
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
    private Role role;

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
        return this.role.getKey();
    }

    public void setRegistrationId(String registrationId){this.registrationId =registrationId;}

    @Builder
    public User(String name, String email, String id, Role role,String registrationId) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.role = role;
        this.registrationId = registrationId;
    }

    public User update(String name) {
        this.name = name;
        this.id = id;
        return this;
    }
}
