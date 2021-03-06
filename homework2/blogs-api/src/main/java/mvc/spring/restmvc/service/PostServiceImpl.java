package mvc.spring.restmvc.service;

import mvc.spring.restmvc.exception.EntityNotFoundException;
import mvc.spring.restmvc.dao.PostRepository;
import mvc.spring.restmvc.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository repo;

    @Override
    @PostFilter("filterObject.authorId == authentication.name or hasAuthority('ALL_POST_READ')")
    public List<Post> getAllPosts() {
        return repo.findAll();
    }

    @Override
    @PostFilter("filterObject.authorId == authentication.name or hasAuthority('ALL_POST_READ')")
    public List<Post> getTop15ByPublishedDesc() {
        return repo.findTop15ByOrderByPublishedDesc();
    }

    @Override
    @PostFilter("filterObject.authorId == authentication.name or hasAuthority('ALL_POST_READ')")
    public List<Post> getAllPostsByStatus(String status) {
        return repo.findByStatus(status);
    }

    @Override
    public List<Post> getByAuthorId(String authorId) {
        return repo.findByAuthorId(authorId);
    }

    @Override
    public Post getPostById(String postId) {
        if (postId == null) return null;
        return repo.findById(postId).orElseThrow(() -> new EntityNotFoundException("Entity with id " + postId + " was not found."));
    }

    @Override
    @PreAuthorize("hasAuthority('ALL_POST_CREATE')")
    public Post createPost(Post post) {
        return repo.insert(post);
    }

    @Override
    public Post updatePost(Post post) {
        return repo.save(post);
    }

    @Override
    public Post deletePost(String postId) {
        Post old = getPostById(postId);
        repo.deleteById(postId);
        return old;
    }
}
