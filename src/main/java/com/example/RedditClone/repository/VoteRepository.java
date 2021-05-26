package com.example.RedditClone.repository;

import com.example.RedditClone.model.Post;
import com.example.RedditClone.model.User;
import com.example.RedditClone.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
