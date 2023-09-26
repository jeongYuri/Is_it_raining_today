package com.example.crudw.demo.Notification;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.comment.Comment;

public class Notification {
    private Comment comment;
    private String user;
    private Board board;

    public Notification(Comment comment, String user, Board board) {
        this.comment = comment;
        this.user = user;
        this.board = board;
    }
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Comment getComment() {//댓글 작성시 boardNo를 받아와야함 -> 그래야 boardNo의 writerName에게 알림
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static Notification from(Notification notification) {
        String message =  notification.getBoard().getTitle() + "'에 댓글을 달았습니다.";
        return new Notification(notification.getComment(), notification.getUser(), notification.getBoard());
    }


}
