package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.Service.UserService;
import com.example.crudw.demo.comment.Comment;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.List;


@Slf4j
@Controller
public class HomeController {

    private final UserService userService;
    private final BoardService boardService;
    private final CommentService commentService;


    @Autowired
    public HomeController(UserService userService, BoardService boardService,CommentService commentService) {
        this.userService = userService;
        this.boardService = boardService;
        this.commentService = commentService;
    }

    @GetMapping(value = "/")
    public String home() {
        return "index";
    }

    @GetMapping(value = "/signup")
    public String join() {
        return "signup";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }
    @GetMapping(path="/logout")
    public String login(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/index";
    }
   @PostMapping(path = "/checkLogin")
   public String checkLogin(@RequestParam(value="id")String id,
                            @RequestParam(value="pw") String pw,
                            Model model,HttpServletRequest request,@ModelAttribute UserForm form){
       //boolean result = userService.login(id,pw);
       User loginUser = userService.login(id, pw);
       String page;

       if(loginUser==null){
           model.addAttribute("msg","로그인 정보가 맞지않습니다.");
           model.addAttribute("url","login");
           page="alert";

       }else{
            id = form.getId();
            pw = form.getPw();
            HttpSession session = request.getSession();
            session.setAttribute("id",id);
            session.setAttribute("pw",pw);
            System.out.println(id);
            page="redirect:/list";
       }
       return page;
   }
    @PostMapping(value = "/signup")
    public String register(@ModelAttribute UserForm form) {
        System.out.println("hi");
        User users = new User();
        users.setId(form.getId());
        users.setPw(form.getPw());
        users.setName(form.getName());
        users.setEmail(form.getEmail());
        users.setPhone(form.getPhone());
        System.out.println(form.getId());
        userService.join(users);
        return "redirect:/";
    }

    /*@PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute UserForm form, BindingResult bindingResult,
                        HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "/login";
        }
        String id = form.getId();
        String pw = form.getPw();
        User loginUser = userService.login(id, pw);
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }

        Cookie cookie = new Cookie("userId", String.valueOf(loginUser.getId()));
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println("쿠키 이름: " + cookie.getName());
        System.out.println("쿠키 값: " + cookie.getValue());
        return "loginhome";

    }*/

    @GetMapping(value = "/write")
    public String write() {
        return "write";

    }

    @GetMapping(value = "/list")
    public String list(Model model) {
        //List<Board> boards = boardService.BoardList();
        model.addAttribute("nboard", boardService.BoardList());
        return "list";
    }
    @PostMapping(value = "/write")
    public String boardwrite(Board board) {
        //HttpSession session = request.getSession();
        //String id = (String)session.getAttribute("id");
        boardService.savePost(board);
        System.out.println(board);
        return "list";
    }

    @GetMapping(value = "/read/{no}")
    public String read(@PathVariable("no") Long no, Model model,Comment comment) {
        List<Comment> commentlist = commentService.getCommentList(no);
        model.addAttribute("board", boardService.getPost(no));
        model.addAttribute("comment",commentlist);
        model.addAttribute("comments", commentService.getComment(comment.getNo()));
        model.addAttribute("board_no",no);
        return "detailboard";
    }

    @GetMapping("/update/post/{no}")
    public String update(@PathVariable("no") Long no, Model model) {
        Board board = boardService.getPost(no);
        model.addAttribute("board", board);
        return "update";
    }
    @GetMapping("/updateComment/{no}") //조회
    public String updateComment(@PathVariable("no")Long no,Model model,@RequestParam("content") String content){
        Comment comment = commentService.getComment(no);
        model.addAttribute("comment",comment);
        return "updateCommentForm";
    }
    @PostMapping(value="/saveComment") //생성
    public String saveupComment(@ModelAttribute Comment comment,Model model){
        commentService.getComment(comment.getNo());
        //model.addAttribute(commentService.commentUpdate(comment));
        //model.addAttribute(comment.getNo());
        return"redirect:/list";
    }

    @PostMapping(value = "/savePost")
    public String savePost(@ModelAttribute Board board, Model model) {
        boardService.getPost(board.getNo());
        // boardService.boardupdate(board);
        model.addAttribute(boardService.boardupdate(board));
        model.addAttribute(board.getNo());
        return "redirect:/list";
    }
    @GetMapping("/deletePost/{no}")
    public String deletePost(@PathVariable("no") Long no, Model model) {
        boardService.deletePost(no);
        return "redirect:/list";
    }
    @PostMapping(value="/comment")
    public String saveComment(@ModelAttribute Comment comment, Model model, HttpServletRequest request) {
        if (comment == null) {

            return "redirect:/list";  // 예시로 리다이렉트 처리하였습니다.
        }
        String page = "/read/" + comment.getBoard_no();
        commentService.saveComment(comment);
        System.out.println("ok");
        HttpSession session = request.getSession();
        String name = (String) session.getAttribute("name");
        Long wirterNo = (Long)session.getAttribute("no");
        comment.setWriter_name(name);
        comment.setWriter_no(wirterNo);
        page = "redirect:" + page;
        return page;
     }
    @GetMapping("/deleteComment/{no}")
    public String deleteComment(@PathVariable("no") Long no, Model model, HttpServletRequest request,Comment comment) {
        commentService.deleteComment(no);
        return "redirect:/list";
    }


}





