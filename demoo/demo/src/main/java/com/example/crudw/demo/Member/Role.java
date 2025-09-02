package com.example.crudw.demo.Member;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER"),
    DEFAULT("USER"); // 기본값 설정

    private final String key;

    Role(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
