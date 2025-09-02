package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Heart.Heart;
import com.example.crudw.demo.Heart.HeartService;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Member.UserUpdate;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.Service.UserService;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentRequestDto;
import com.example.crudw.demo.comment.CommentWithBoardTitle;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.crudw.demo.Member.Role;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
public class HomeController {
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


    @GetMapping(value = "/")
    public String home() {
        return "index";
    }

    @GetMapping(value="/loginhome")
    public String loginhome(){return "loginhome";}



    @PostMapping(path = "/checkLogin")
    public String checkLogin(@RequestParam(value="id")String id,
                             @RequestParam(value="pw") String pw,
                             Model model,HttpServletRequest request,@ModelAttribute UserForm form){
        boolean result = userService.login(id, pw);
        String page;

        if(result == false){
            model.addAttribute("msg","로그인 정보가 맞지않습니다.");
            model.addAttribute("url","login");
            return "alert";
        }

        User user = userService.getUser(id);
        if (user == null) {
            model.addAttribute("msg", "사용자 정보를 찾을 수 없습니다.");
            model.addAttribute("url", "login");
            return "alert";
        }

        HttpSession session = request.getSession();
        session.setAttribute("id",id);
        session.setAttribute("pw",pw);
        session.setAttribute("no", user.getNo()); //글 작성할때 넣어야해서...user정보랑 같이 넣는 느낌..?

        String sessionId = session.getId(); // 세션 ID를 얻어옵니다
        Object no = session.getAttribute("no");

        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //SecurityUser users = (SecurityUser) authentication.getPrincipal();
        //String username = (authentication != null) ? authentication.getName() : "Anonymous";
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = authentication.getName();
        model.addAttribute("username", username);
        //String username = authentication.getName();
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("username", username); // 사용자 ID를 모델에 추가
        String res = "redirect:/";
        return res;
    }

    @ResponseBody
    @GetMapping("/login/oauth2/code/kakao")
    public void  kakaoCallback(@RequestParam String code) {

        System.out.println(code);

    }

    @GetMapping(value = "/findIdPw")
    public String findid() {
        return "findIdPw";
    }



    @GetMapping("/myBoardList")
    public String myBoard(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String name = (String) session.getAttribute("name");
        List<Board> myBoards = new ArrayList<>();
        User user = null;
        List<CommentWithBoardTitle> commentsWithBoardTitle = null;
        if (id != null) {
            user = userService.getUser(id);
            myBoards = boardService.myboard(id);
            commentsWithBoardTitle = commentService.CommentsWithBoardTitle(id);
        } else if (name != null) {
            user = userService.getUserName(name);
            myBoards = boardService.myboard(name);
            commentsWithBoardTitle = commentService.CommentsWithBoardTitle(name);
        }

        model.addAttribute("myBoard", user);
        model.addAttribute("myBoards", myBoards);
        model.addAttribute("Comments", commentsWithBoardTitle);

        return "myBoardList";
    }






}







