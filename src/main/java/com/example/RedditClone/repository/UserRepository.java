package com.example.RedditClone.repository;

import com.example.RedditClone.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserName(String userName);
//    List<User> findByEmail(String email);
     Boolean existsByUserName (String userName);
     Boolean existsByEmail(String email);

}
