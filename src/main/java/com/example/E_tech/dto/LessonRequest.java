package com.example.E_tech.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LessonRequest {
    private String title;
    private String contentType;
    private String contentUrl;    // for video
    private String textContent;   // for text
    private MultipartFile file;   // for PDF
    private String moduleId;

}
