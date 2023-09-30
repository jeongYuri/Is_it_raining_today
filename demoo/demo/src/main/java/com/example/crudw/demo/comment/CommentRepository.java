package com.example.crudw.demo.comment;

import com.example.crudw.demo.Board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("SELECT c, b.title FROM Comment c JOIN Board b ON c.boardNo = b.no WHERE c.writerName = :writerName")
    List<Object[]> findCommentsWithBoardTitleByWriterName(@Param("writerName") String writerName);

    @Query("SELECT b.title FROM Comment c JOIN Board b ON c.boardNo = b.no WHERE c.boardNo = :boardNo")
    List<Object[]> findBoardTitleByBoardNo(@Param("boardNo") Long boardNo);

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
