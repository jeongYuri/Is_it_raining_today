package com.example.crudw.demo.Repository;

import com.example.crudw.demo.comment.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Comment commentupdate(Comment comment);
    void deleteById(Long no);
    List<Comment> findByBoardNoOrderByParentNoAscNoAsc(Long board_no);
    Optional<Comment> findByNo(Long no);
}
