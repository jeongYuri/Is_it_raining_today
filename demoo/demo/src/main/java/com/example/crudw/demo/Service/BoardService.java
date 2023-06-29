package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Repository.BoardRepository;
import com.example.crudw.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public class BoardService {

    private final BoardRepository boardRepository;
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    public List<Board> BoardList() {
        return boardRepository.findAll();
    }
    public Board getPost(Long no) {
        Optional<Board> boardOpt = boardRepository.findById(no);
        Board board = boardOpt.get();
        return board;
    }
    public Long savePost(Board board) {
        return boardRepository.insert(board).getNo();
    }

}
