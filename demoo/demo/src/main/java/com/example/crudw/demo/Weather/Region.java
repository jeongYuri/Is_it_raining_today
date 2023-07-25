package com.example.crudw.demo.Weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
public class Region {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="code")
    private String code;

    public Region(String id, String code) {
        this.id = id;
        this.code = code;
    }

    public Region() {

    }
}
