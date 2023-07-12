package com.example.crudw.demo.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    private Long board_no;
    private Long parent_no=0L;
    private Long writer_no;
    private String writer_name;
    private String content;
    private LocalDateTime create_time;
    private LocalDateTime modify_time;

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public Long getParent_no() {
        return parent_no;
    }

    public void setParent_no(Long parent_no) {
        this.parent_no = parent_no;
    }

    public Long getWriter_no() {
        return writer_no;
    }

    public void setWriter_no(Long writer_no) {
        this.writer_no = writer_no;
    }

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
    }

    public LocalDateTime getModify_time() {
        return modify_time;
    }

    public void setModify_time(LocalDateTime modify_time) {
        this.modify_time = modify_time;
    }

    public Long getBoard_no() {
        return board_no;
    }

    public void setBoard_no(Long board_no) {
        this.board_no = board_no;
    }
}
