package com.example.RedditClone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Data
@Document(collection = "Comments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String commentId;
    @NotEmpty
    private String comment;
    @DBRef
    private Post post;
    private Instant createdDate;
    @DBRef
    private User user;
}
