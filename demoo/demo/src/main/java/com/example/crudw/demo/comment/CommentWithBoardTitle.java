package com.example.crudw.demo.comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class CommentWithBoardTitle {
    private String boardTitle;  // 게시글 제목
    private String writerName;  // 댓글 작성자
    private String content;     // 댓글 내용
    private Long boardNo;       // 게시글 번호
    private LocalDateTime modifyTime; // 댓글 수정 시간
}
