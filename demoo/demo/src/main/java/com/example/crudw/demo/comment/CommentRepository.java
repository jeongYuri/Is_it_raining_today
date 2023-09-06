package com.example.crudw.demo.comment;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByBoardNoOrderByParentNoAscNoAsc(Long boardNo);
    Comment findByNo(Long no);
    List<Comment> findAllByBoardNo(Long boardNo);



}
