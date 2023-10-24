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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

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
    @PostMapping(path = "/checkLogin")
    public String checkLogin(@RequestParam(value="id")String id,
                             @RequestParam(value="pw") String pw,
                             Model model,HttpServletRequest request,@ModelAttribute UserForm form){
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

            String sessionId = session.getId(); // 세션 ID를 얻어옵니다

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //SecurityUser users = (SecurityUser) authentication.getPrincipal();
            String username = authentication.getName();
            model.addAttribute("sessionId", sessionId);
            model.addAttribute("username", username); // 사용자 ID를 모델에 추가
            page="redirect:/";
        }
        return page;
    }
    @ResponseBody
    @GetMapping("/login/oauth2/code/kakao")
    public void  kakaoCallback(@RequestParam String code) {

        System.out.println(code);

    }
    @GetMapping("/socialLogin")
    public String socialLoginPage(@RequestParam String provider) {
        if ("google".equals(provider)) {
            return "redirect:/login/oauth2/code/google";
        } else if ("naver".equals(provider)) {
            return "redirect:/login/oauth2/code/naver";
        } else if ("kakao".equals(provider)){
            return "redirect:/login/oauth2/code/kakao";
        }
        return "redirect:/login";
    }
    @GetMapping("/login/oauth2/code/{registrationId}")
    public String googleLogin(@RequestParam String code, @PathVariable String registrationId) {
        userService.socialLogin(code, registrationId);
        return "redirect:/";
    }
    @GetMapping("/oauth/loginInfo")
    public String oauthLoginInfo(Authentication authentication,Model model,HttpSession session) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes.toString());
        model.addAttribute("name",attributes.get("name"));
        session.setAttribute("name", attributes.get("name"));
        System.out.println(attributes.get("nickname"));
        System.out.println(attributes.get("name"));
        return "redirect:/";
    }
    @GetMapping(value = "/findIdPw")
    public String findid() {
        return "findIdPw";
    }
    @PostMapping(path="/findId", produces = "application/json")
    @ResponseBody
    public Map<String, String> findId(@RequestParam(value = "name") String name,
                                      @RequestParam(value = "email") String email) {
        Map<String, String> response = new HashMap<>();
        User foundUsers = userService.findId(name, email);
        if (foundUsers != null) {
            response.put("foundUser", foundUsers.getId());
        } else {
            response.put("notfound", "true");
        }
        return response;
    }
    @GetMapping(path="/logout")
    public String login(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate(); //로그아웃시 세션 만료
        return "redirect:/";
    }



    @GetMapping(value = "/write")
    public String write() {
        return "write";
    }

    @GetMapping(value = {"/list", "/search"})
    public String listAndSearch(@RequestParam(value = "searchStr", required = false) String searchStr,
                                @RequestParam(value = "searchType",required = false) String searchType,
                                Model model,
                                @PageableDefault(size = 10, sort = "no", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> boardPage;
        System.out.println(searchStr + searchType);

        if (searchStr != null && !searchStr.isEmpty()) { //searchStr가 비어있지않다면 검색기능 수행
            List<Board> searchList = boardService.search(searchStr,searchType);
            boardPage = new PageImpl<>(searchList, pageable, searchList.size());
            model.addAttribute("searchList", searchList);
        } else {//searchStr가 비어있다면 그냥 페이징 처리된거 보여줘야징
            boardPage = boardService.pageList(pageable);
        }

        int startPage = Math.max(1, boardPage.getPageable().getPageNumber() - 10); //10개의 게시글 보여줘야지
        int endPage = Math.min(boardPage.getTotalPages(), boardPage.getPageable().getPageNumber() + 10);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasNext", boardPage.hasNext()); //다음페이지
        model.addAttribute("hasPrev", boardPage.hasPrevious()); //이전페이지
        model.addAttribute("nboard", boardPage);
        return "list"; // 검색 + 페이징 처리 통합
    }


    @GetMapping(value = "/read/{no}")
    public String read(@PathVariable("no") Long no, Model model, @ModelAttribute Comment comment, HttpServletRequest request, Heart heart) {
        List<CommentRequestDto> commentList = commentService.getComments(no);
        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute("id");
        boardService.hit(no);

        model.addAttribute("like",heartService.findLike(no,userId)); //게시글 읽을때 좋아요 눌렀는지 확인?
        model.addAttribute("board", boardService.getPost(no));
        model.addAttribute("comment",commentList); //댓글을 보여주기
        model.addAttribute("comments",comment);
        model.addAttribute("heart",heart);
        model.addAttribute("board_no",no);
        return "detailboard";
    }

    @GetMapping("/updateUser")
    public String updateUser(Model model, HttpServletRequest request){
        HttpSession session  = request.getSession();
        String id = (String)session.getAttribute("id");
        String name = (String)session.getAttribute("name");
        User user = null;
        if (id != null) {
            user = userService.getUser(id);
        } else if (name != null) {
            user = userService.getUserName(name);

        }
        model.addAttribute("user",user);
        return "updateUser";
    }
    @PostMapping("/saveUser")
    public String saveUser( HttpServletRequest request, Model model, @ModelAttribute UserUpdate updateDTO){
        //Long no = userService.saveUser(user);
        HttpSession session = request.getSession();
        String id = (String)session.getAttribute("id");
        String name = (String)session.getAttribute("name");

        if (id != null) {
            userService.updateUser(id, updateDTO);
            User updatedUser = userService.getUser(id);
            model.addAttribute("user", updatedUser);
        } else if (name != null) {
            userService.updatesocialUser(name, updateDTO);
            User updatesocialUser = userService.getUserName(name);
            model.addAttribute("user", updatesocialUser);
        }

        //userService.updateUser(id, updateDTO); //정보 업데이트
       // userService.updateUser(name,updateDTO);
        //User updatedUser = userService.getUser(id);

        model.addAttribute("msg", "정보 수정에 성공하였습니다.");
        model.addAttribute("url", "mypage");

        return "myPage";
    }

    @GetMapping("/deleteUser")
    public String deleteUser( Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String name = (String)session.getAttribute("name");
        //String writerName = (String) session.getAttribute("id");//id 값이 writername 같기 떄문에ㅣ.!
        if (id != null) {
            userService.deleteUser(id);
        } else if (name != null) {
            userService.deleteSocialUser(name);
        }
        //userService.deleteUser(id);
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping(path="/myPage")
    public String myPage(Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        String id = (String)session.getAttribute("id");
        String name = (String)session.getAttribute("name");
        User user = userService.getUser(id);
        User socialuser = userService.getUserName(name);

        if (user == null && socialuser==null) {
            model.addAttribute("msg", "로그인 후 이용가능");
        } else {
            if (id != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("user", socialuser);
            }
        }
        return "myPage";
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







