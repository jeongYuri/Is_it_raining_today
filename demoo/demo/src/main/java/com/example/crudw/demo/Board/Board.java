package com.example.crudw.demo.Board;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
@Data
@Entity
public class Board {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    private Long writer_no;
    private String writer_name;
    private String title;
    private String content;
    private String create_time;
    private String modify_time;
    private int hit;
    private String file_name;
    private String file_link;

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_link() {
        return file_link;
    }

    public void setFile_link(String file_link) {
        this.file_link = file_link;
    }
    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }
}

