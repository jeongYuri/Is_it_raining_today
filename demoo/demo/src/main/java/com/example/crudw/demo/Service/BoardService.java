package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Repository.BoardRepository;
import org.springframework.transaction.annotation.Transactional;

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
    public Long boardupdate(Board board){return boardRepository.boardupdate(board).getNo();}

    public Board getPost(Long no) {
        Optional<Board> boardOpt = boardRepository.findById(no);
        Board board = boardOpt.get();
        boardRepository.hit(no);
        return board;
    }


    public List<Board> getPostsByUserId(String writerName) {
        return boardRepository.findByWriterName(writerName);
    }
    public Long savePost(Board board) {
        return boardRepository.insert(board).getNo();
    }
    public void deletePost(Long no){boardRepository.deleteById(no);
    }




}
