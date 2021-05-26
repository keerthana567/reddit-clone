package com.example.RedditClone.repository;

import com.example.RedditClone.model.Comment;
import com.example.RedditClone.model.Post;
import com.example.RedditClone.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
