package com.example.crudw.demo.Member;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserForm {
    private String id;
    private String no;
    private String loginId;
    private String pw;
    private String name;
    private String email;
    private String phone;
    private String role;
}
