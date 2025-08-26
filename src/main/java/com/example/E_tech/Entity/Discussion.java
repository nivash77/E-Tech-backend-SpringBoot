package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "discussions")
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {
    @Id
    private String id;

    private String courseId;
    private String userId;
    private String content;
    private String parentId;
    private Integer likes=0;
    private Set<String> likedUserIds = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
