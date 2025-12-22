package vehicle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vehicle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/vehicles";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, 
                          @RequestParam String password,
                          Model model) {
        try {
            if (username == null || username.trim().length() < 3) {
                model.addAttribute("error", "Имя пользователя должно содержать минимум 3 символа");
                model.addAttribute("username", username);
                return "register";
            }
            
            if (password == null || password.length() < 4) {
                model.addAttribute("error", "Пароль должен содержать минимум 4 символа");
                model.addAttribute("username", username);
                return "register";
            }
            
            userService.register(username.trim(), password);
            return "redirect:/login?registered";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.");
            model.addAttribute("username", username);
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при регистрации. Пожалуйста, попробуйте позже.");
            model.addAttribute("username", username);
            return "register";
        }
    }
}
