package com.exam.blog.controllers;

import com.exam.blog.models.User;
import com.exam.blog.service.UserRepoImpl;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Map;

@Controller
public class SecurityController {

    private final UserRepoImpl userRepo;

    @Autowired
    public SecurityController(UserRepoImpl userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping(value = {"/login","/authentication"})
    public String loginGet(Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Страница авторизации");
        if(auth == null){
            return "redirect:/accessDenied";
        } else if(!auth.getPrincipal().toString().equalsIgnoreCase("anonymousUser")){
            if(auth.getAuthorities().stream().anyMatch(a-> a.toString().equalsIgnoreCase("ROLE_USER")))
                return "redirect:/user/main";
            if(auth.getAuthorities().stream().anyMatch(a-> a.toString().equalsIgnoreCase("ROLE_ADMIN")))
                return "redirect:/admin/main";
        }
        return "sign_in";
    }

    @PostMapping(value = {"/authentication"})
    public String loginPost(@RequestParam(name = "error", required = false) Boolean error,
                            @RequestParam(name = "message", required = false) String message,
                            Model model){

        if(Boolean.TRUE.equals(error)){
            model.addAttribute("error" , true);
            model.addAttribute("message", message);
        }
        return "sign_in";

    }

    @GetMapping("/registration")
    public String registrationGet(Model model){
        model.addAttribute("title", "Страница регистрации");
        Map post = model.asMap();
        for(Object key : post.keySet()){
            if(key != "title") {
                model.addAttribute(key.toString(), post.get(key));
            }
        }

        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute User user,
                                   RedirectAttributes redirectAttributes){

        if(Arrays.asList(userRepo.userRegistration(user)).contains(null)) {
            User userDB = userRepo.getUserByUserName(user.getUsername());
            if (userDB == null && user.getUsername() != null) {
                userRepo.saveBoolean(user);
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("account", true);
                redirectAttributes.addFlashAttribute("msg", "Такой аккаунт уже существует!\n Попробуйте еще раз");
                return "redirect:/registration";
            }
        } else {
            redirectAttributes.addFlashAttribute("bool", true);
            redirectAttributes.addFlashAttribute("msg", userRepo.userRegistration(user));
            return "redirect:/registration";
        }
    }
}
