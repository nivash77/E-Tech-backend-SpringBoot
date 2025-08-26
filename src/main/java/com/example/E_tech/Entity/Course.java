package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "courses")
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;

    private String title;
    private String description;
    private String teacherId;
    private String thumbnail;
    private double price;
    private LocalDateTime createdAt;
    private List<Enrollment> enrollments = new ArrayList<>();
    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
