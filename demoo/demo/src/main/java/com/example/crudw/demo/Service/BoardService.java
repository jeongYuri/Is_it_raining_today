package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardUpdate;
import com.example.crudw.demo.Board.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    @Autowired
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }
    @Transactional
    public Page<Board> pageList(Pageable pageable){
        return boardRepository.findAll(pageable);
    }
    public List<Board> BoardList() {
        List<Board> boardList = boardRepository.findAllByOrderByNoDesc();
        return boardList;
    }
    public List<Board> getBoardList(String searchType, String searchStr) {
        List<Board> boardList;

        if (searchType.equals("작성자"))
            boardList = boardRepository.findByWriterNameContainingOrderByNoDesc(searchStr);
        else if (searchType.equals("제목"))
            boardList = boardRepository.findByTitleContainingOrderByNoDesc(searchStr);
        else
            boardList = boardRepository.findByContentContainingOrderByNoDesc(searchStr);

        return boardList;
    }
    //public Long boardupdate(Board board){return boardRepository.boardupdate(board).getNo();}

    public Board getPost(Long no) {
        Optional<Board> boardOpt = boardRepository.findById(no);
        Board board = boardOpt.get();

        return board;
    }
    public List<Board> getPostsByUserId(String writerName) {
        return boardRepository.findByWriterName(writerName);
    }

    @Transactional
    public Long savePost(Board board) {

        return boardRepository.save(board).getNo();
    }
    @Transactional
    public void deletePost(Long no){boardRepository.deleteById(no);
    }
    @Transactional
    public Board updateBoard(Long boardId, Board board) {
        Board boardUpdate = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        if (board.getTitle() != null) {
            boardUpdate.setTitle(board.getTitle());
        }
        if (board.getContent() != null) {
            boardUpdate.setContent(board.getContent());
        }
        if (board.getFileName() != null) {
            boardUpdate.setFileName(board.getFileName());
        }
        if (board.getFileLink() != null) {
            boardUpdate.setFileLink(board.getFileLink());
        }

        return boardRepository.save(boardUpdate); // boardUpdate를 DB에 저장
    }
    @Transactional
    public int hit(Long no){
        return boardRepository.hit(no);
    }

    @Transactional
    public List<Board> search(String searchStr,String searchType){
        List<Board> boardList;
        if("title".equals(searchType)){
            boardList= boardRepository.findByTitleContainingOrderByNoDesc(searchStr);
        }else if("content".equals(searchType)){
            boardList= boardRepository.findByContentContainingOrderByNoDesc(searchStr);
        }else if("writer".equals(searchType)){
            boardList= boardRepository.findByWriterNameContainingOrderByNoDesc(searchStr);
        }else{
            boardList = Collections.emptyList(); //검색이 없으면~
        }
        return boardList;
    }

    @Transactional
    public List<Board> myboard(String id){//내가쓴 글 보기
        List<Board> myBoardList = boardRepository.findBywriterNameOrderByNoDesc(id);

        return myBoardList;
    }


}
