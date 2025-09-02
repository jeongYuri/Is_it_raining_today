package com.example.crudw.demo.Controller;

import com.example.crudw.demo.Member.Role;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserUpdate;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.UserService;
import com.example.crudw.demo.Board.Board;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
@Slf4j
@Controller
public class UserController {
    private final UserService userService;
    private final BoardService boardService;

    //생성자 주입 방식으로 변경
    public UserController(UserService userService,BoardService boardService){
        this.userService = userService;
        this.boardService = boardService;
    }

    @GetMapping(value = "/signup")
    public  String signup(){
        return "signup";
    }



    @PostMapping(path = "/addUser")
    public String addUser(@ModelAttribute User user, Model model){
        boolean exist = userService.getUser(user.getId()) != null;

        if (user.getRole() == null) {
            user.setRole(Role.DEFAULT);  // setRole을 사용해서 기본값을 설정
        }
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
    public String login(){
        return "login";
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
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/socialLogin")
    public String socialLoginPage(@RequestParam String provider) {
        if ("naver".equals(provider)) {
            return "redirect:/login/oauth2/code/naver";
        } else if ("kakao".equals(provider)){
            return "redirect:/login/oauth2/code/kakao";
        }else{
            return "redirect:/login/oauth2/code/google";
        }

    }
    @GetMapping("/login/oauth2/code/{registrationId}")
    public String googleLogin(@RequestParam String code, @PathVariable String registrationId) {
        userService.socialLogin(code, registrationId);
        return "redirect:/";
    }

    @GetMapping("/oauth/loginInfo")
    public String oauthLoginInfo(Authentication authentication, Model model, HttpSession session) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes.toString());
        model.addAttribute("name",attributes.get("name"));
        session.setAttribute("name", attributes.get("name"));
        return "redirect:/";
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
    public String myPage(Model model, HttpServletRequest request, BoardService boardService){
        HttpSession session = request.getSession();
        String id = (String)session.getAttribute("id");
        String name = (String)session.getAttribute("name");
        //User user = userService.getUser(id);
        //User socialuser = userService.getUserName(name);
        User user = null;
        User socialuser = null;
        if(id!=null){
            user = userService.getUser(id);
        }else if(name!=null){
            socialuser = userService.getUserName(name);
        }
        if (user != null) {
            model.addAttribute("user", user);
            List<Board> myBoards = this.boardService.myboard(user.getId());
            //model.addAttribute("myBoards", myBoards);
        } else if (socialuser != null) {
            model.addAttribute("user", socialuser);
            List<Board> myBoards = this.boardService.myboard(socialuser.getId());
            model.addAttribute("myBoards", myBoards);
        } else {
            model.addAttribute("msg", "로그인 후 이용 가능합니다.");
            model.addAttribute("url", "login");
            return "alert";
        }

        return "myPage";
    }
}


