package com.example.crudw.demo.Service;

import com.example.crudw.demo.Repository.CommentRepository;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentUpdate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private  CommentRepository commentRepository;

   // public CommentService(CommentRepository commentRepository) {
        //this.commentRepository = commentRepository;
   // }

    public List<Comment> getCommentList(Long board_no){
        List<Comment> commentList = commentRepository.findByBoardNoOrderByParentNoAscNoAsc(board_no);
        return commentList;
    }

    public Comment getComment(Long no){
        Comment commentOpt = commentRepository.findByNo(no);
        return commentOpt;
    }
    /*
    public List<Comment> getComment(String writerName){
       return commentRepository.findById(writerName);

    }*/
    //public void deleteCommentById(String writerName){commentRepository.deleteByUserId(writerName);}

    public Long saveComment(Comment comment){return commentRepository.save(comment).getNo();}

    public void deleteComment(Long no){commentRepository.deleteById(no);}
    @Transactional
    public Comment updateComment(Long boardId, CommentUpdate updateDTO) {
        Comment comment = commentRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (updateDTO.getContent() != null) {
            comment.setContent(updateDTO.getContent());
        }

        return commentRepository.save(comment);
    }

}
