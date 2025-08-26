package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "assignments")
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    private String id;

    private String moduleId;
    private String courseId;
    private String title;
    private String description;
    private String teacherId;
    private LocalDateTime deadline;
    private Integer maxPoints;
}
