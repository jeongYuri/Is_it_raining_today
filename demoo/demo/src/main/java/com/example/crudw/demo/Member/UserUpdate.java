package com.example.crudw.demo.Member;

import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "users")
public class UserUpdate {
        private String id;
        private String pw;
        private String name;
        private String email;
        private String phone;
}

