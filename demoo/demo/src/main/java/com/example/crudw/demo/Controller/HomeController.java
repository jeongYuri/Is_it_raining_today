package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardUpdate;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Member.UserUpdate;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.Service.UserService;
import com.example.crudw.demo.Weather.Region;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentUpdate;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;


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
    EntityManager em;


    @GetMapping(value = "/")
    public String home() {
        return "index";
    }
    @GetMapping(value="/loginhome")
    public String loginhome(){return "loginhome";}
    @GetMapping(value = "/signup")
    public String signup(Model model) {
        return "signup"; // 오타 수정
    }
    @PostMapping(path="/addUser")
    public String addUser(@ModelAttribute User user, Model model) {
        boolean exist = userService.getUser(user.getId()) != null;

        if (exist == true) {
            model.addAttribute("msg", "회원가입에 실패하였습니다.");
            model.addAttribute("url", "signup");
        } else {
            Long no = userService.saveUser(user);
            model.addAttribute("msg", "회원가입에 성공하였습니다.");
            model.addAttribute("url", "login");
        }

        return "alert";
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
       boolean result = userService.login(id, pw);
       String page;

       if(result == false){
           model.addAttribute("msg","로그인 정보가 맞지않습니다.");
           model.addAttribute("url","login");
           page="alert";

       }else{
           User user = userService.getUser(id);
            HttpSession session = request.getSession();
            session.setAttribute("id",id);
            session.setAttribute("pw",pw);
           session.setAttribute("no", user.getNo()); //글 작성할때 넣어야해서...user정보랑 같이 넣는 느낌..?
            System.out.println(id);
            page="redirect:/";
       }
       return page;
   }


    @GetMapping(value = "/write")
    public String write() {
        return "write";

    }

    @GetMapping(value = {"/list", "/search"})
    public String listAndSearch(@RequestParam(value = "searchStr", required = false) String searchStr,
                                Model model,
                                @PageableDefault(size = 5, sort = "no", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> boardPage;

        if (searchStr != null && !searchStr.isEmpty()) { //searchStr가 비어있지않다면 검색기능 수행
            List<Board> searchList = boardService.search(searchStr);
            boardPage = new PageImpl<>(searchList, pageable, searchList.size());
            model.addAttribute("searchList", searchList);
        } else {//searchStr가 비어있다면 그냥 페이징 처리된거 보여줘야징
            boardPage = boardService.pageList(pageable);
        }

        int startPage = Math.max(1, boardPage.getPageable().getPageNumber() - 5);
        int endPage = Math.min(boardPage.getTotalPages(), boardPage.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasNext", boardPage.hasNext());
        model.addAttribute("hasPrev", boardPage.hasPrevious());
        model.addAttribute("nboard", boardPage);

        return "list"; // 검색 + 페이징 처리 통합
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

        board.setFileName(file_name);
        board.setFileLink(file_link);
        boardService.savePost(board);
        return "redirect:/list";
    }

    @GetMapping(value = "/read/{no}")
    public String read(@PathVariable("no") Long no, Model model,Comment comment,Board board) {
        List<Comment> commentlist = commentService.getCommentList(no);
        boardService.hit(no);
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
    public String updateUser(Model model, HttpServletRequest request){
        HttpSession session  = request.getSession();
        String id = (String)session.getAttribute("id");
        User user = userService.getUser(id);
        model.addAttribute("user",user);
        return "updateUser";
    }
    @PostMapping("/saveUser")
    public String saveUser( HttpServletRequest request, Model model, @ModelAttribute UserUpdate updateDTO){
        //Long no = userService.saveUser(user);
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");

        userService.updateUser(id, updateDTO);
        User updatedUser = userService.getUser(id);

        model.addAttribute("user", updatedUser);
        model.addAttribute("msg", "정보 수정에 성공하였습니다.");
        model.addAttribute("url", "mypage");

        return "myPage";
    }

    @GetMapping("/update/post/{no}")
    public String update(@PathVariable("no") Long no, Model model) {
        Board board = boardService.getPost(no);
        model.addAttribute("board", board);
        return "update";
    }

    @PostMapping(value = "/savePost")
    public String savePost(@ModelAttribute Board board, Model model, @ModelAttribute BoardUpdate updateDTO) {
        //boardService.getPost(board.getNo());
        boardService.updateBoard(board.getNo(), updateDTO);
        //model.addAttribute(boardService.boardupdate(board));
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
        String page = "/read/" + comment.getBoardNo();
        commentService.saveComment(comment);
        System.out.println("ok");
        HttpSession session = request.getSession();
        String name = (String) session.getAttribute("name");
        Long wirterNo = (Long)session.getAttribute("no");
        comment.setWriterName(name);
        comment.setWriterNo(wirterNo);
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
    public String saveupComment(@ModelAttribute Comment comment, Model model, @ModelAttribute CommentUpdate updateDTO) {
        commentService.updateComment(comment.getNo(), updateDTO);
        String page = "/read/" + comment.getBoardNo();
        System.out.println("/read/" + comment.getBoardNo());
        //commentService.commentUpdate(comment);
        //return "redirect:" + page;
        return "redirect:/list";
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
            if (board.getFileLink() != null) {
                Path filePath = Paths.get( board.getFileLink());
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 게시글 삭제
            boardService.deletePost(board.getNo());
            //commentService.deleteCommentById(writerName);
        }
        userService.deleteUser(id);
        session.invalidate();
        return "redirect:/";
    }
    @GetMapping("/download/{no}")
    public ResponseEntity<InputStreamResource> fileDownload(@PathVariable("no") Long no) throws IOException {
        Board board = boardService.getPost(no);

        Path path = Paths.get(board.getFileLink());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        String fileName = new String(board.getFileName().getBytes("UTF-8"), "ISO-8859-1");

        System.out.println(board.getFileName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + board.getFileName())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }




}







