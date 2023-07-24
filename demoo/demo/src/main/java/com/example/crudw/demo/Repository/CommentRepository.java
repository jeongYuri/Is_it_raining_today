package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.comment.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Comment commentupdate(Comment comment);
    List<Comment> findAll();
    void deleteById(Long no);
    void deleteByUserId(String writerName);
   // List<Comment> findById(String writerName);
    List<Comment> findByBoardNoOrderByParentNoAscNoAsc(Long board_no);
    Optional<Comment> findByNo(Long no);
}
