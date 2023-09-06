package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.BoardRepository;
import com.example.crudw.demo.TimeEntity;
import com.example.crudw.demo.comment.CommentRepository;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentRequestDto;
import com.example.crudw.demo.comment.CommentUpdate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardRepository boardRepository;

    public List<Comment> getCommentList(Long board_no) {
        List<Comment> commentList = commentRepository.findByBoardNoOrderByParentNoAscNoAsc(board_no); //board 번호로 commentlist
        return commentList;
    }

    @Transactional
    public List<CommentRequestDto> getComments(Long boardNo) { //게시글 가져오기~
        List<Comment> comments = commentRepository.findAllByBoardNo(boardNo);//해당게시글 댓글 조회
        List<CommentRequestDto> commentRequestDtoList = CommentRequestDtoList(comments);//comments를 CommentRequestDto로 변환
        return commentRequestDtoList;
    }
    private List<CommentRequestDto>CommentRequestDtoList(List<Comment> comments) {
        List<CommentRequestDto> commentRequestDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentRequestDto cmrd = new CommentRequestDto();
            cmrd.setNo(comment.getNo());
            cmrd.setContent(comment.getContent());
            cmrd.setWriterName(comment.getWriterName());
            cmrd.setWriterNo(comment.getWriterNo());
            cmrd.setBoardNo(comment.getBoardNo());
            cmrd.setCreateTime(comment.getCreateTime()); // 생성 시간 추가
            cmrd.setModifyTime(comment.getModifyTime()); //

            if (comment.getParentNo() == null) {
                commentRequestDtoList.add(cmrd);
            } else {
                CommentRequestDto parentDto = commentRequestDtoList.stream()
                        .filter(dto -> dto.getNo().equals(comment.getParentNo().getNo()))
                        .findFirst()
                        .orElse(null);

                if (parentDto != null) {
                    parentDto.getChildren().add(cmrd);
                }
            }
        }
        return commentRequestDtoList;
    }

    public Comment getComment(Long no) {
        Comment commentOpt = commentRepository.findByNo(no);
        return commentOpt;
    }

    /*
    public List<Comment> getComment(String writerName){
       return commentRepository.findById(writerName);

    }*/
    //public void deleteCommentById(String writerName){commentRepository.deleteByUserId(writerName);}
    @Transactional
    public Comment saveComment(CommentRequestDto commentRequestDto) {
        Comment comment = Comment.builder()
                        .boardNo(commentRequestDto.getBoardNo())
                        .content(commentRequestDto.getContent())
                        .writerNo(commentRequestDto.getWriterNo())
                        .writerName(commentRequestDto.getWriterName())
                        .build();

        if(commentRequestDto.getParentNo()!=null){//부모 번호가 있다는것!!
                 Comment parentComment = commentRepository.findByNo(commentRequestDto.getParentNo());
                 comment.updateParent(parentComment);
        }
        return commentRepository.save(comment); //저장
    }
    @Transactional
    public void deleteComment(Long no) {
        Comment comment = commentRepository.findCommentByNo(no);
        if (comment.getChildren().size()!= 0) {
            comment.changeIsDeleted(true);//자식이 있으면 상태만 변경..
            commentRepository.save(comment);
        } else {
            commentRepository.delete(getDeleteableParent(comment));

        }
    }
        private Comment getDeleteableParent(Comment comment){ //삭제 가능 조상 찾기
            Comment parentNo = comment.getParentNo();//댓글의 부모 구하기
            if(parentNo!=null && parentNo.getChildren().size() == 1 && parentNo.getIsDeleted()==true)
                return getDeleteableParent(parentNo);
        return comment;
    }





    @Transactional
    public Comment updateComment(Long boardId, CommentUpdate updateDTO) {
        Comment comment = commentRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        if (updateDTO.getContent() != null) { //댓글 수정한다면
            comment.setContent(updateDTO.getContent());
        }
        return commentRepository.save(comment);
    }

}
