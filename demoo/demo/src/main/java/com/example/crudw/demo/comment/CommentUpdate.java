package com.example.crudw.demo.comment;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "comment")
public class CommentUpdate {

    private String content;
}
