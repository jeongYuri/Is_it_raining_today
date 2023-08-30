package com.example.crudw.demo.Board;

import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "nboard")
public class Board extends TimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long no;
    @Column(name = "writer_no")
    private Long writerNo;
    @Column(name = "writer_name")
    private String writerName;
    private String title;
    private String content;
    @Column(columnDefinition = "integer default 0",nullable = false)
    private int hit;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_link")
    private String fileLink;


    public Long getNo() {
        return no;
    }
    public void setNo(Long no) {
        this.no = no;
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
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileLink() {
        return fileLink;
    }
    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public Long getWriterNo() {
        return writerNo;
    }

    public void setWriterNo(Long writerNo) {
        this.writerNo = writerNo;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }


}

