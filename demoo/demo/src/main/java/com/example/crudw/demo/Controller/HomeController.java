package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.UserService;
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

import java.util.List;

@Slf4j
@Controller
public class HomeController {

    private final UserService userService;
    private final BoardService boardService;


    @Autowired
    public HomeController(UserService userService,BoardService boardService) {
        this.userService = userService;
        this.boardService = boardService;
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
    public String loginhome() {
        return "login";
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
    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute UserForm form, BindingResult bindingResult,
                        HttpServletResponse response) {

        if (bindingResult.hasErrors()){
            return "/login";
        }
        String id = form.getId();
        String pw = form.getPw();
        User loginUser = userService.login(id, pw);
        if(loginUser == null){
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }

        Cookie cookie = new Cookie("userId", String.valueOf(loginUser.getId()));
        response.addCookie(cookie);
        return "loginhome";

    }
    @GetMapping(value = "/write")
    public String write() {
        return "write";

    }
    @GetMapping(value = "/list")
    public String list(Model model) {
        //List<Board> boards = boardService.BoardList();
        model.addAttribute("nboard",boardService.BoardList());
        return "list";
    }

    @PostMapping(value = "/write")
    public String boardwrite(Board board){
        //HttpSession session = request.getSession();
        //String id = (String)session.getAttribute("id");
        boardService.savePost(board);
        System.out.println(board);
        return "loginhome";

    }
    @GetMapping(value="/read/{no}")
    public String read(@PathVariable("no") Long no,Model model){
        //Board board = boardService.getPost(no);
        //Cookie[] cookies = request.getCookies();
        //boolean flag = true;
        model.addAttribute("board",boardService.getPost(no));
        System.out.println(no);
   //     model.addAttribute("no",no);
        return "detailboard";
    }
    @RequestMapping("/update/post/{no}")
    public String update(@PathVariable("no") Long no, Model model) {
        //boardService.boardupdate(board);
       // boardService.boardupdate(board);
        Board board = boardService.getPost(no);
        model.addAttribute("board",board);
        return "update";
    }

    @RequestMapping(value="/savePost",method = RequestMethod.POST)
    public String boardupdate(Board board,Model model){
        model.addAttribute(boardService.boardupdate(board));
        return "redirect:/read";
    }


}


