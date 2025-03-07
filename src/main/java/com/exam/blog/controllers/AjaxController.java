package com.exam.blog.controllers;

import com.exam.blog.models.Blog;
import com.exam.blog.models.Comment;
import com.exam.blog.models.Ingredient;
import com.exam.blog.models.User;
import com.exam.blog.service.BlogService;
import com.exam.blog.service.CommentService;
import com.exam.blog.service.IngredientService;
import com.exam.blog.service.UserRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

/**
 @author Zhurenko Evgeniy
 */

@Controller
@RequestMapping("/")
public class AjaxController {

    private final BlogService blogService;
    private final UserRepoImpl userRepo;
    private final CommentService commentService;
    private final IngredientService ingredientService;


    @Autowired
    public AjaxController(BlogService blogService, UserRepoImpl userRepo, CommentService commentService, IngredientService ingredientService) {
        this.blogService = blogService;
        this.userRepo = userRepo;
        this.commentService = commentService;
        this.ingredientService = ingredientService;
    }


    // change rating of blog
    @GetMapping(value = "ajax/rating")
    public ResponseEntity<Float> ratingBlog(@RequestParam(value = "idBlog", required = false) String id_blog,
                                            @RequestParam(value = "rating", required = false) String str_rating,
                                            Model model) {

        Long idBlog = Long.valueOf(id_blog);
        float rating = Float.valueOf(str_rating);
        Blog blogDB = blogService.getById(idBlog);
        if (blogDB != null) {
            if (blogDB.getRating() == null || blogDB.getRating() == 0F) {
                blogDB.setRating(rating);
            } else {
                Float ratingDB = (blogDB.getRating() + rating) / 2;
                ratingDB =  Math.round(ratingDB*100.0F)/100.0F;
                blogDB.setRating(ratingDB);
            }
            blogService.update(blogDB);
        }

        model.addAttribute("blog", blogService.getById(idBlog));

        return ResponseEntity.ok(blogDB.getRating());
    }

    // ban of blog
    @GetMapping(value = "ajax/ban/blog")
    public ResponseEntity<Boolean> banBlog(@RequestParam(value = "idBlog", required = false) String id_blog,
                                            @RequestParam(value = "bunBlog", required = false) String str_ban) {
        Long idBlog = Long.valueOf(id_blog);
        Boolean banBlog = Boolean.valueOf(str_ban);

        Blog blogDB = blogService.getById(idBlog);
        blogDB.setBan_blog(banBlog);
        blogService.update(blogDB);

        return ResponseEntity.ok(banBlog);
    }

    // ban user
    @GetMapping(value = "ajax/ban/user")
    public ResponseEntity<Boolean> banUser(@RequestParam(value = "idUser", required = false) String id_user,
                                           @RequestParam(value = "bunUser", required = false) String str_ban) {
        Long idUser = Long.valueOf(id_user);
        Boolean banUser = Boolean.valueOf(str_ban);

        User userDB = userRepo.getById(idUser);
        userDB.setBan_user(banUser);
        userRepo.update(userDB,false);

        return ResponseEntity.ok(banUser);
    }

    // ban comment
    @GetMapping(value = "ajax/ban/comment")
    public ResponseEntity<Boolean> banComment(@RequestParam(value = "idComment", required = false) String id_comment,
                                              @RequestParam(value = "bunComment", required = false) String str_ban) {
        Long idComment = Long.valueOf(id_comment);
        Boolean banComment = Boolean.valueOf(str_ban);

        Comment commentDB = commentService.getById(idComment);
        commentDB.setBanComment(banComment);
        commentService.update(commentDB);

        return ResponseEntity.ok(banComment);
    }

    //delete ingredient
    @GetMapping(value = "ajax/delete-ingredient")
    public ResponseEntity<Boolean> deleteIngredientBlog(@RequestParam(value = "idBlog",required = false) String id_blog,
                                                        @RequestParam(value = "idIngredient", required = false) String id_ingredient){

        Long idBlog = Long.valueOf(id_blog);
        Long idIngredient = Long.valueOf(id_ingredient);
        boolean bool = false;
        Blog blogDB = blogService.getById(idBlog);
        Ingredient ingredientDB = ingredientService.getById(idIngredient);
        if(blogDB != null){
            if(blogDB.getIngredients().contains(ingredientDB)){
                blogDB.getIngredients().remove(ingredientDB);
                blogService.update(blogDB);
                ingredientService.deleteIngredient(ingredientDB.getId());
                bool = true;
            }
        }
        return ResponseEntity.ok(bool);
    }

    // не передает User в Comment-ax
//    @GetMapping(value = "ajax/show/all-comment-blog/{idBlog}")
//    public ResponseEntity<List<Comment>> showAllComment(@PathVariable(value = "idBlog", required = false) Long idBlog){
//
//        List<Comment> allCommentOfBlog = blogService.getById(idBlog).getComments();
//
//        return ResponseEntity.ok(allCommentOfBlog);
//    }


//    @GetMapping(value = "ajax/comment")
//    public ResponseEntity<Comment> addCommentBlog(@RequestParam(value = "idBlog", required = false) String idBlog,
//                                                        @RequestParam(value = "name", required = false) String username,
//                                                        @RequestParam(value = "comment", required = false) String comment,
//                                                        @RequestParam(value = "idUser", required = false) String idUser,
//                                                        Model model) {
//
//        System.out.println(Long.valueOf(idBlog) + " " + username + " " + comment + " " + Long.valueOf(idUser));
//        Blog blogDB = blogService.getById(Long.valueOf(idBlog));
//        Comment commentUser = new Comment();
//        commentUser.setBlog(blogDB);
//        commentUser.setDateCreateComment(LocalDateTime.now());
//        commentUser.setText(comment);
//        commentUser.setBanComment(false);
//        commentUser.setUser(userRepo.getById(Long.valueOf(idUser)));
//        commentService.save(commentUser);
//
//        System.out.println(ResponseEntity.ok(commentUser));
//
//        return ResponseEntity.ok().body(commentUser);
//    }
}
