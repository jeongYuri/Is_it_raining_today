package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class HomeController {

    private final UserService userService;


    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;}

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

        // 로그인 기능 수행
        User loginUser = userService.login(id, pw);

        //글로벌 에러 발생
        if(loginUser == null){
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }
        // 성공 로직
        Cookie cookie = new Cookie("userId", String.valueOf(loginUser.getId()));
        response.addCookie(cookie);
        return "redirect:/";
    }
}


