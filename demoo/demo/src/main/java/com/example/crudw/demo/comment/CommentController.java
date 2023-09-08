package com.example.crudw.demo.comment;

import com.example.crudw.demo.Heart.HeartService;
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
        Comment comment = commentService.getComment(no);// 댓글 번호이용하여
        Long boardNo = comment.getBoardNo();//게시물 번호 가져오기
        String page = "/read/" + boardNo;
        commentService.deleteComment(no); //댓글삭제
        page = "redirect:" + page;
        return  page;
    }

}
