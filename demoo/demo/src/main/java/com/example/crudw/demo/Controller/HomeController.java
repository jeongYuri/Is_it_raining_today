package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.Service.UserService;
import com.example.crudw.demo.comment.Comment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
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
    @GetMapping(value="/loginhome")
    public String loginhome(){return "loginhome";}
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
        return "redirect:/";
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
            page="redirect:/";
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
    public String boardwrite(Board board,@RequestParam(required = false,name= "file") MultipartFile file) {
        //HttpSession session = request.getSession();
        //String id = (String)session.getAttribute("id");
        String file_name = null;
        String file_link = null;
        if (file!=null) {
            file_name = file.getOriginalFilename();
            if (file_name != null && file_name.isEmpty()) {
                file_name = null;
            }
            if(file_name!=null){
                String saveFileName = "";
                Calendar calendar = Calendar.getInstance();
                saveFileName += calendar.get(Calendar.YEAR);
                saveFileName += calendar.get(Calendar.MONTH);
                saveFileName += calendar.get(Calendar.DATE);
                saveFileName += calendar.get(Calendar.HOUR);
                saveFileName += calendar.get(Calendar.MINUTE);
                saveFileName += calendar.get(Calendar.SECOND);
                saveFileName += calendar.get(Calendar.MILLISECOND);
                saveFileName += file_name;
                file_link = "C:\\Users\\dydrk\\crud_webp\\upload\\" + saveFileName;
                try {
                    file.transferTo(new File(file_link));
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        board.setFile_name(file_name);
        board.setFile_link(file_link);
        boardService.savePost(board);
        return "redirect:/list";
    }

    @GetMapping(value = "/read/{no}")
    public String read(@PathVariable("no") Long no, Model model,Comment comment) {
        List<Comment> commentlist = commentService.getCommentList(no);
        model.addAttribute("board", boardService.getPost(no));
        model.addAttribute("comment",commentlist);
        model.addAttribute("comments",comment);
        model.addAttribute("board_no",no);
        return "detailboard";
    }
    @GetMapping(path="/myPage")
    public String mypage(Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        String id = (String)session.getAttribute("id");
        User user = userService.getUser(id);
        if(user ==null){
            model.addAttribute("msg","로그인 후 이용가능");
        }else{
            model.addAttribute("user",user);
        }
        return "myPage";
    }

    @GetMapping("/updateUser")
    public String updateUser(Model model,HttpServletRequest request){
        HttpSession session  = request.getSession();
        String id = (String)session.getAttribute("id");
        User user = userService.getUser(id);
        model.addAttribute("user",user);
        return "updateUser";
    }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, Model model){
        if (user.getNo() == null) {
            model.addAttribute("errorMsg", "사용자 번호가 없습니다.");
            return "updateUser";
        }
        System.out.println(user);
        model.addAttribute("user",  userService.userUpdate(user));
        //model.addAttribute(user.getNo());
        return "redirect:/myPage";
    }

    @GetMapping("/update/post/{no}")
    public String update(@PathVariable("no") Long no, Model model) {
        Board board = boardService.getPost(no);
        model.addAttribute("board", board);
        return "update";
    }

    @PostMapping(value = "/savePost")
    public String savePost(@ModelAttribute Board board, Model model) {
        boardService.getPost(board.getNo());
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

            return "redirect:/list";
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
    @GetMapping("/updateComment/{no}") // 조회
    public String updateComment(@PathVariable("no") Long no, Model model) {
        Comment comment = commentService.getComment(no);
        model.addAttribute("comment", comment);
        return "updateCommentForm";
    }

    @PostMapping(value = "/saveComment") //생성
    public String saveupComment(@ModelAttribute Comment comment, Model model) {
        String page = "/read/" + comment.getBoard_no();
        commentService.commentUpdate(comment);
        page = "redirect:" + page;
        return page;
    }
    @GetMapping("/deleteComment/{no}")
    public String deleteComment(@PathVariable("no") Long no, Model model, HttpServletRequest request,Comment comment) {
        commentService.deleteComment(no);
        return "redirect:/list";
    }

    @GetMapping("/deleteUser")
    public String deleteUser( Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String writerName = (String) session.getAttribute("id");//id 값이 writername 같기 떄문에ㅣ.!
        List<Board> userBoards = boardService.getPostsByUserId(writerName);

        // 회원의 모든 게시글 삭제
        for (Board board : userBoards) {
            // 파일이 존재하는 경우 파일도 삭제
            if (board.getFile_link() != null) {
                Path filePath = Paths.get("C:/Temp/" + board.getFile_link());
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 게시글 삭제
            boardService.deletePost(board.getNo());
        }
        userService.deleteUser(id);
        session.invalidate();
        return "redirect:/";
    }
    @GetMapping("/download/{no}")
    public ResponseEntity<InputStreamResource> fileDownload(@PathVariable("no") Long no) throws IOException {
        Board board = boardService.getPost(no);

        Path path = Paths.get(board.getFile_link());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        String fileName = new String(board.getFile_name().getBytes("UTF-8"), "ISO-8859-1");

        System.out.println(board.getFile_name());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + board.getFile_name())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
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

}





