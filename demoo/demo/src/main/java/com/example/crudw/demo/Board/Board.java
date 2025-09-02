package com.example.crudw.demo.Board;

import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
@Getter
@Entity
@Table(name = "nboard")
public class Board extends TimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long no;
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
    @Column(name = "like_count",nullable = false,columnDefinition = "integer default 0") // 원하는 컬럼명으로 설정
    private int likeCount;


    public Long getNo() {
        return no;
    }
    public void setNo(Long no) {
        this.no = no;
    }

    public Long writerNo() {return writerNo;}
    public void setWriterNo(Long writerNo) {this.writerNo = writerNo;}
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



    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    // 기본 생성자 추가
    public Board() {
    }
    // 생성자 추가 (필요한 필드만 초기화....)
    public Board(String title, String writerName, String content,Long writerNo) {
        this.title = title;
        this.writerNo = writerNo;
        this.writerName = writerName;
        this.content = content;
        this.hit = 0;  // 기본값 설정
        this.likeCount = 0; // 기본값 설정
    }

}

