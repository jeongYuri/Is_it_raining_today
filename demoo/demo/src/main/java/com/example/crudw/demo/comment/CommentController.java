package com.example.crudw.demo.comment;

import com.example.crudw.demo.Heart.HeartService;
import com.example.crudw.demo.Notification.NotificationService;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.Service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class CommentController {
    @Autowired
    UserService userService;
    @Autowired
    BoardService boardService;
    @Autowired
    CommentService commentService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    HeartService heartService;
    @Autowired
    EntityManager em;

    @PostMapping(value="/comment")
    public String saveComment(@ModelAttribute CommentRequestDto commentRequestDto) {
        commentService.saveComment(commentRequestDto);
        String page = "/read/" + commentRequestDto.getBoardNo();
        System.out.println(page);
        page = "redirect:" + page;
        return page;
    }

    @GetMapping("/updateComment/{no}") // 조회
    public String updateComment(@PathVariable("no") Long no, Model model) {
        Comment comment = commentService.getComment(no);
        model.addAttribute("comment", comment);
        return "detailboard";
    }

    @PostMapping(value = "/saveComment") //생성
    public String saveupComment(@ModelAttribute CommentRequestDto commentRequestDto, @ModelAttribute CommentUpdate updateDTO) {
        Comment comment = commentService.updateComment(commentRequestDto.getNo(), updateDTO);
        String page = "/read/" + comment.getBoardNo();
        page = "redirect:" + page;
        return page;
    }
    @GetMapping("/deleteComment/{no}")
    public String deleteComment(@PathVariable("no") Long no) {
        Comment comment = commentService.getComment(no);
        Long boardNo = comment.getBoardNo();
        String page = "/read/" + boardNo;
        notificationService.deleteByCommentNo(no);//댓글을 작성하면 알림이 울리게 됨으로 댓글 삭제시 알림삭제하기로 함
        commentService.deleteComment(no);
        page = "redirect:" + page;
        return page;
    }

}
