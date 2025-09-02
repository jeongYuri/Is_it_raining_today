package com.example.crudw.demo.comment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommentRequestDto {
    private Long no;
    private String writerName;
    private String content;
    private Long boardNo;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private Long parentNo;
    private List<CommentRequestDto> children = new ArrayList<>();
    private Long writerNo;

    public CommentRequestDto() {}

    // setter 추가
    public void setNo(Long no) {
        this.no = no;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public void setWriterNo(Long writerNo) {
        this.writerNo = writerNo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBoardNo(Long boardNo) {
        this.boardNo = boardNo;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setParentNo(Long parentNo) {
        this.parentNo = parentNo;
    }

    public void addChild(CommentRequestDto child) {
        this.children.add(child);
    }
}
