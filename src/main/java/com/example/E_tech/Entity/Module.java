package com.example.E_tech.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "modules")
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    @Id
    private String id;

    private String courseId;
    private String title;
    private Integer order;
}
