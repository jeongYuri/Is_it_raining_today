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

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
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
    public Long savePost(Board board) {
        return boardRepository.save(board).getNo();
    }
    public void deletePost(Long no){boardRepository.deleteById(no);
    }
    @Transactional
    public Board updateBoard(Long boardId, BoardUpdate updateDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (updateDTO.getTitle() != null) {
            board.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getContent() != null) {
            board.setContent(updateDTO.getContent());
        }
        if (updateDTO.getFileName() != null) {
            board.setFileName(updateDTO.getFileName());
        }
        if (updateDTO.getFileLink() != null) {
            board.setFileLink(updateDTO.getFileLink());
        }

        return boardRepository.save(board);
    }
    @Transactional
    public int hit(Long no){
        return boardRepository.hit(no);
    }

    @Transactional
    public List<Board> search(String searchStr){
        List<Board> boardList= boardRepository.findByTitleContainingOrderByNoDesc(searchStr);
        return boardList;
    }


}
