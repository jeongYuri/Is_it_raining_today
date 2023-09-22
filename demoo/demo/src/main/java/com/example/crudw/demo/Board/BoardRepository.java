package com.example.crudw.demo.Board;
import com.example.crudw.demo.Board.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BoardRepository extends JpaRepository<Board,Long> {

    Board findByNo(Long no);
    boolean existsBywriterName(String writerName);

    @Modifying
    @Query("update Board  set hit = hit +1 where no = :no")
    int hit(@Param("no") Long no);
    @Modifying
    @Query("UPDATE Board SET likeCount = likeCount + 1 WHERE no=:no")
    void incrementCount(@Param("no") Long no);

    @Modifying
    @Query("UPDATE Board SET likeCount = likeCount -1 WHERE no=:no")
    void deleteCount(@Param("no") Long no);

    //Optional<Board>findByNo(Long no);
    Optional<Board> findById(Long no);

    List<Board> findByWriterName(String writer_name);

    void deleteById(Long no);
    void deleteByWriterName(String writerName);

    List<Board> findAllByOrderByNoDesc();
    List<Board> findBywriterNameOrderByNoDesc(String writerName);

    // Page<Board> findAll(Pageable pageable);
    List<Board> findByWriterNameContainingOrderByNoDesc(String searchStr); //검색하기

    List<Board> findByTitleContainingOrderByNoDesc(String searchStr);  //검색하기

    List<Board> findByContentContainingOrderByNoDesc(String searchStr); //검색하기


}
