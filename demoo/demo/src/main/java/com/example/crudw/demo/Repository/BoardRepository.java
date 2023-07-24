package com.example.crudw.demo.Repository;
import com.example.crudw.demo.Board.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    Board insert(Board board);
    Board boardupdate(Board board);
    void hit(Long no);
    List<Board> findAll();
    //Optional<Board>findByNo(Long no);
    Optional<Board> findById(Long no);
    List<Board> findByWriterName(String writerName);
    void deleteById(Long no);
}
