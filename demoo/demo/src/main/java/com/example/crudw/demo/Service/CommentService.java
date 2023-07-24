package com.example.crudw.demo.Service;

import com.example.crudw.demo.Repository.CommentRepository;
import com.example.crudw.demo.comment.Comment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentList(Long board_no){
        List<Comment> commentList = commentRepository.findByBoardNoOrderByParentNoAscNoAsc(board_no);
        return commentList;
    }
    public Long commentUpdate(Comment comment){
        Comment Comment = commentRepository.commentupdate(comment);
        return Comment.getBoard_no();
    }
    public Comment getComment(Long no){
        Optional<Comment> commentOpt = commentRepository.findByNo(no);
        return commentOpt.orElse(null);
    }
    /*
    public List<Comment> getComment(String writerName){
       return commentRepository.findById(writerName);

    }*/
    public void deleteCommentById(String writerName){commentRepository.deleteByUserId(writerName);}

    public Long saveComment(Comment comment){return commentRepository.save(comment).getNo();}

    public void deleteComment(Long no){commentRepository.deleteById(no);}

}
