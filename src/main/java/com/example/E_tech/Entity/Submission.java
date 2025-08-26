package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "submissions")
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    @Id
    private String id;

    private String assignmentId;
    private String studentId;
    private String fileId;
    @CreatedDate
    private LocalDateTime submittedAt;
    private Integer grade;
    private String feedback;

    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
