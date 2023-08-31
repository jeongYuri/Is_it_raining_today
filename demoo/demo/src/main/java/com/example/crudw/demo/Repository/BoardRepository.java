package com.example.crudw.demo.Repository;
import com.example.crudw.demo.Board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {


    @Modifying
    @Query("update Board  set hit = hit +1 where no = :no")
    int hit(@Param("no") Long no);
    //Optional<Board>findByNo(Long no);
    Optional<Board> findById(Long no);
    List<Board> findByWriterName(String writer_name);
    void deleteById(Long no);
    List<Board> findAllByOrderByNoDesc();
   // Page<Board> findAll(Pageable pageable);
    List<Board> findByWriterNameContainingOrderByNoDesc(String searchStr);

    List<Board> findByTitleContainingOrderByNoDesc(String searchStr);  //검색하기

    List<Board> findByContentContainingOrderByNoDesc(String searchStr);
}
