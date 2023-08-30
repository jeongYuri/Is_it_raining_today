package com.example.crudw.demo.Board;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "nboard")
public class BoardUpdate {
    private String title;
    private String content;
    private int hit;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_link")
    private String fileLink;
}
