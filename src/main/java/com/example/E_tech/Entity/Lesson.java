package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "lessons")
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    private String id;
    private String moduleId;
    private String title;
    private String contentText;
    private String contentType;
    private String contentUrl;
}
