package com.example.crudw.demo.Repository;
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Member.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    Board insert(Board board);
    List<Board> findAll();
    Optional<Board> findById(Long no);
}
