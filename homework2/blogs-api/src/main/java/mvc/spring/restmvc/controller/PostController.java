package mvc.spring.restmvc.controller;

import lombok.extern.slf4j.Slf4j;
import mvc.spring.restmvc.forms.PostForm;
import mvc.spring.restmvc.service.PostService;
import mvc.spring.restmvc.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class PostController {

    private static final String UPLOADS_DIR = "tmp";

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/api/posts/form", method = RequestMethod.GET)
    public String createPost(PostForm postForm) {
        return "posts/create_post";
    }

    @RequestMapping(value = "/api/posts", method = RequestMethod.GET)
    public String getPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        if (posts.isEmpty()) {
            return "posts/list";
        }
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @RequestMapping(value = "/api/posts/view/{postId}", method = RequestMethod.GET)
    public String getPost(@PathVariable String postId, Model model) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return "redirect:/";
        }
        model.addAttribute("post", post);
        log.info(post.toString());
        return "posts/view";
    }

    @RequestMapping(value = "/api/posts/edit/{postId}", method = RequestMethod.GET)
    public String showEditForm(@PathVariable("postId") String postId, Model model) {
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        return "posts/edit_post";
    }

    @RequestMapping(value = "/api/posts/update/{postId}", method = RequestMethod.POST)
    public String updatePost(@PathVariable("postId") String postId, @Valid Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().toString());
            return "posts/edit_post";
        }
        post.setId(postId);
        postService.updatePost(post);
        model.addAttribute("post", post);
        return "redirect:/api/posts/view/" + post.getId();
    }

    @RequestMapping(value = "/api/posts/delete/{postId}", method = RequestMethod.GET)
    public String deletePost(@PathVariable("postId") String postId, Model model) {
        Post post = postService.getPostById(postId);
        postService.deletePost(postId);
        model.addAttribute("posts", postService.getAllPosts());
        return "posts/list";
    }

    @RequestMapping(value = "/api/posts", method = RequestMethod.POST)
    public String addPost(@Valid PostForm postForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "posts/create_post";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setText(postForm.getText());
        post.setTags(postForm.getTags());
        post.setAuthorId(auth.getName());
        Post created = postService.createPost(post);

        model.addAttribute("post", created);
        return "redirect:/api/posts/view/" + created.getId();
    }

    private void handleMultipartFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        long size = file.getSize();
        log.info("File: " + name + ", Size: " + size);
        try {
            File currentDir = new File(UPLOADS_DIR);
            if(!currentDir.exists()) {
                currentDir.mkdirs();
            }
            String path = currentDir.getAbsolutePath() + "/" + file.getOriginalFilename();
            path = new File(path).getAbsolutePath();
            log.info(path);
            File f = new File(path);
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(f));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}