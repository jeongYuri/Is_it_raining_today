package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Repository.BoardRepository;

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
    public Board boardupdate(Board board){return boardRepository.boardupdate(board);
    }

        public Board getPost(Long no) {
        System.out.println(no);
        Optional<Board> boardOpt = boardRepository.findById(no);
        Board board = boardOpt.get();
        return board;
    }
    public Long savePost(Board board) {
        return boardRepository.insert(board).getNo();
    }

}
