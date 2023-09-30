package com.example.crudw.demo.Notification;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.TimeEntity;
import com.example.crudw.demo.comment.Comment;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name="notification")
public class Notification extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long no;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @JoinColumn(name = "user_from")
    private String writer;

    @JoinColumn(name = "user_to")
    private String user;
    private String message;




    @Builder
    public Notification(Comment comment, String user, Board board,String message) {
        this.comment = comment;
        this.user = user;
        this.board = board;
        this.message = message;

    }

}
