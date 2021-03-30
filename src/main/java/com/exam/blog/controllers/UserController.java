package com.exam.blog.controllers;

import com.exam.blog.models.Blog;
import com.exam.blog.models.User;
import com.exam.blog.service.BlogService;
import com.exam.blog.service.UserRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/")
public class UserController {

    private final UserRepoImpl userRepo;
    private final BlogService blogService;

    @Autowired
    public UserController(UserRepoImpl userRepo, BlogService blogService) {
        this.userRepo = userRepo;
        this.blogService = blogService;
    }

    @GetMapping("blog/{id_user}/{id_blog}")
    public String blogUser(@PathVariable(value = "id_user", required = false) Long id_user,
                           @PathVariable(value = "id_blog", required = false) Long id_blog,
                           Model model){
        User user = userRepo.getById(id_user);
        Blog blog = blogService.getById(id_blog);
        model.addAttribute("boolean", true);
        model.addAttribute("title", blog.getTitle());
        model.addAttribute("blog", blog);
        model.addAttribute("user", user);
        return "blog-list";
    }

    @GetMapping("{id}")
    public String userInfo(@PathVariable(name = "id", required = false) Long idUser,
                           Model model){

        User user = userRepo.getById(idUser);
        model.addAttribute("title", "Страница пользователя");
        model.addAttribute("user", user);

        return "user/user_page";
    }

    @GetMapping("update/{id}")
    public String userUpdate(@PathVariable(name = "id", required = false) Long idUser,
                           Model model){

        User user = userRepo.getById(idUser);
        model.addAttribute("title", "Страница редактирования данных пользователя");
        model.addAttribute("user", user);
        model.addAttribute("rating", user.getBlogs().stream().map(Blog::getRating).collect(Collectors.toList()))

        return "user/user_update_page";
    }
}
