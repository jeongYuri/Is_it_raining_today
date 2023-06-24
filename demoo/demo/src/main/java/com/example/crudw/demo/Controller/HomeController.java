package com.example.crudw.demo.Controller;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserForm;
import com.example.crudw.demo.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping(value="/")
    public String home(){
        return "index";
    }

    @GetMapping(value = "/signup")
    public String join(){return "signup";}

    @PostMapping (value ="/signup")
    public String register(@ModelAttribute UserForm form){
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

    @GetMapping(value = "/login")
    public String login(Model model){return "login";}
}
