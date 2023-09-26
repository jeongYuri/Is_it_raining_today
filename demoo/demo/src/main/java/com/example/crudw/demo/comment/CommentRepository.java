package com.example.crudw.demo.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByBoardNoOrderByParentNoAscNoAsc(Long boardNo);
    List<Comment>findByParentNo(Comment paretNo);
    Comment findByNo(Long no);
    List<Comment> findByWriterName(String writerName);
    Comment findCommentByNo(Long no);
    List<Comment> findAllByBoardNo(Long boardNo);

    boolean existsByBoardNo(Long no);
    boolean existsByWriterName(String writerName);
    void deleteByBoardNo(Long no);
    void deleteByWriterName(String writerName);
}
