package com.example.E_tech.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private String id;
    private String title;
    private String moduleId;
    private List<Question> questions;

    @Data
    public static class Question {
        private String questionText;
        private String type;
        private List<String> options;
        private List<String> correctAnswers;
    }
}
