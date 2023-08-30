package com.example.crudw.demo.comment;

import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment extends TimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long no;
    @Column(name = "board_no")
    private Long boardNo;
    @Column(name = "parent_no")
    private Long parentNo = 0L;
    @Column(name = "writer_no")
    private Long writerNo;
    @Column(name = "writer_name")
    private String writerName;
    private String content;

    public Long getNo() {
        return no;
    }
    public void setNo(Long no) {
        this.no = no;
    }
    public Long getBoardNo() {
        return boardNo;
    }
    public void setBoardNo(Long boardNo) {
        this.boardNo = boardNo;
    }
    public Long getParentNo() {
        return parentNo;
    }
    public void setParentNo(Long parentNo) {
        this.parentNo = parentNo;
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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
