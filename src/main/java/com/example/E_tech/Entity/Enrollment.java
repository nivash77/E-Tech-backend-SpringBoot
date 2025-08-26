package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "enrollments")
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    private String id;

    private String userId;
    private String courseId;
    private List<String> completedLessons = new ArrayList<>();
    private List<String> completedAssignments = new ArrayList<>();
    private double progress = 0;
    private List<QuizResult> quizResults = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuizResult {
        private String quizId;
        private int score;
    }
}
