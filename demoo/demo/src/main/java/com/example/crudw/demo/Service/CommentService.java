package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardRepository;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserRepository;
import com.example.crudw.demo.Notification.NotificationService;
import com.example.crudw.demo.comment.*;
import com.example.crudw.demo.config.auth.dto.SessionUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;


    public List<Comment> getCommentList(Long board_no) {
        List<Comment> commentList = commentRepository.findByBoardNoOrderByParentNoAscNoAsc(board_no); //board 번호로 commentlist
        return commentList;
    }

    @Transactional
    public List<CommentRequestDto> getComments(Long boardNo) { //게시글의 댓글가져오기~
        List<Comment> comments = commentRepository.findAllByBoardNo(boardNo);//해당게시글 댓글 조회
        List<CommentRequestDto> commentRequestDtoList = CommentRequestDtoList(comments);//comments를 CommentRequestDto로 변환
        return commentRequestDtoList;
    }
    private List<CommentRequestDto>CommentRequestDtoList(List<Comment> comments) { //dto를 이용하여 parent 유무에 따른 값 저장
        List<CommentRequestDto> commentRequestDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentRequestDto cmrd = new CommentRequestDto();
            cmrd.setNo(comment.getNo());
            cmrd.setContent(comment.getContent());
            cmrd.setWriterName(comment.getWriterName());
            cmrd.setWriterNo(comment.getWriterNo());
            cmrd.setBoardNo(comment.getBoardNo());
            cmrd.setCreateTime(comment.getCreateTime()); // 생성 시간 추가
            cmrd.setModifyTime(comment.getModifyTime()); //수정시간

            if (comment.getParentNo() == null) {//부모가 없다는 뜻
                commentRequestDtoList.add(cmrd); //부모가 비었다? 그냥 저장
            } else {
                CommentRequestDto parentDto = findParent(commentRequestDtoList, comment.getParentNo().getNo());//부모가 있으니 부모 번호를 가져와서 저장
                if (parentDto != null) {
                    parentDto.addChild(cmrd);
                    //parentDto.getChildren().add(cmrd);
                }
            }
        }
        return commentRequestDtoList;
    }
    private CommentRequestDto findParent(List<CommentRequestDto> commentRequestDtoList, Long parentNo) {//부모 찾기
        for (CommentRequestDto dto : commentRequestDtoList) {
            if (dto.getNo().equals(parentNo)) {
                return dto;
            } else if (dto.getChildren() != null) {
                CommentRequestDto result = findParent(dto.getChildren(), parentNo);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
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
        //SessionUser sessionUser = (SessionUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //String currentUserName = sessionUser.getName();
        //User user = userRepository.findByName(currentUserName);
        //commentRequestDto.setWriterNo(user.getNo());
        System.out.println("된다");
        Long writerNo = null;
        if (commentRequestDto.getWriterNo() == null || commentRequestDto.getWriterNo() <= 0) {
            User user = userRepository.findByName(commentRequestDto.getWriterName());
            if (user != null) {
                writerNo = user.getNo();
            }
        } else {
            // writerNo가 이미 주어진 경우 그대로 사용합니다
            writerNo = commentRequestDto.getWriterNo();
        }

        Comment comment = Comment.builder()
                        .boardNo(commentRequestDto.getBoardNo())
                        .content(commentRequestDto.getContent())
                        .writerNo(writerNo)
                        .writerName(commentRequestDto.getWriterName())
                        .build();

        if(commentRequestDto.getParentNo()!=null){//부모 번호가 있다는것!!
                 Comment parentComment = commentRepository.findByNo(commentRequestDto.getParentNo());
                 comment.updateParent(parentComment);
        }
        Comment savedComment = commentRepository.save(comment);
        Long boardNo = commentRequestDto.getBoardNo();
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        // 댓글 작성자와 게시글 작성자가 다를 경우에만 알림을 보냄
        if (!commentRequestDto.getWriterName().equals(board.getWriterName())) {
            notificationService.send(board.getWriterName(), savedComment);
            logger.info("알림 전송: 받는 사람={}, 내용={}", board.getWriterName(), commentRequestDto.getContent());
        }
        return savedComment;
    }
    /*
    @Transactional
    public void notifyCommentCreation(CommentRequestDto commentRequestDto) {
        // 댓글을 저장하고 댓글 정보를 가져옴
        Comment savedComment = saveComment(commentRequestDto);
        System.out.println(savedComment + "1212");
        // 게시글 정보를 가져옴 (여기서는 예시로 게시글 번호로 가져오는 것으로 가정)
        Long boardNo = commentRequestDto.getBoardNo();
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        // 댓글 작성자와 게시글 작성자가 다를 경우에만 알림을 보냄
        if (!commentRequestDto.getWriterName().equals(board.getWriterName())) {
            notificationService.send(board.getWriterName(), savedComment, "새로운 댓글이 달렸습니다!");
            logger.info("알림 전송: 받는 사람={}, 내용={}", board.getWriterName(), "새로운 댓글이 달렸습니다!");
        }
    }*/


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
                //부모번호가 null값이 아니라면..상태가 true인 상태라면 삭제 가능
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



    public List<CommentWithBoardTitle> CommentsWithBoardTitle(String writerName) {
        List<Comment> myCommentList = commentRepository.findByWriterName(writerName); // 내 댓글 목록
        List<CommentWithBoardTitle> commentsWithBoardTitle = new ArrayList<>();

        for (Comment comment : myCommentList) {
            List<Object[]> boardTitles = commentRepository.findBoardTitleByBoardNo(comment.getBoardNo());
            if (!boardTitles.isEmpty()) {
                String boardTitle = (String) boardTitles.get(0)[0]; // 첫 번째 원소 추출
                CommentWithBoardTitle commentWithBoardTitle = new CommentWithBoardTitle(boardTitle, comment.getWriterName(), comment.getContent(), comment.getBoardNo(), comment.getModifyTime());
                commentsWithBoardTitle.add(commentWithBoardTitle);
            }
        }

        return commentsWithBoardTitle;
    }
}


