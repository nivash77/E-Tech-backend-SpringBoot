package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Lesson;
import com.example.E_tech.Service.FileService;
import com.example.E_tech.Service.LessonService;
import com.example.E_tech.dto.LessonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Lesson> createLessonFile(
            @ModelAttribute LessonRequest req,
            @RequestHeader("email") String teacherEmail) throws IOException {

        Lesson lesson = new Lesson();
        lesson.setTitle(req.getTitle());
        lesson.setContentType(req.getContentType());
        lesson.setModuleId(req.getModuleId());

        // Upload PDF
        if ("pdf".equals(req.getContentType()) && req.getFile() != null) {
            String fileUrl = fileService.uploadFile(req.getFile());
            lesson.setContentUrl(fileUrl);
        }

        Lesson createdLesson = lessonService.createLesson(lesson, teacherEmail);
        return ResponseEntity.ok(createdLesson);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Lesson> updateLessonFile(
            @PathVariable String id,
            @RequestHeader("email") String teacherEmail,
            @ModelAttribute LessonRequest req) throws IOException {

        Lesson lesson = new Lesson();
        lesson.setTitle(req.getTitle());
        lesson.setContentType(req.getContentType());

        Lesson updatedLesson = lessonService.updateLesson(id, lesson, teacherEmail, req.getFile());
        return ResponseEntity.ok(updatedLesson);
    }


    @PostMapping("/add-json")
    public ResponseEntity<Lesson> createLessonJson(
            @RequestBody LessonRequest req,
            @RequestHeader("email") String teacherEmail) {

        Lesson lesson = new Lesson();
        lesson.setTitle(req.getTitle());
        lesson.setContentType(req.getContentType());
        lesson.setModuleId(req.getModuleId());

        if ("video".equals(req.getContentType())) lesson.setContentUrl(req.getContentUrl());
        if ("text".equals(req.getContentType())) lesson.setContentText(req.getTextContent());

        Lesson createdLesson = lessonService.createLesson(lesson, teacherEmail);
        return ResponseEntity.ok(createdLesson);
    }

    @PutMapping("/{id}/update-json")
    public ResponseEntity<Lesson> updateLessonJson(
            @PathVariable String id,
            @RequestBody LessonRequest req,
            @RequestHeader("email") String teacherEmail) {

        Lesson lesson = new Lesson();
        lesson.setTitle(req.getTitle());
        lesson.setContentType(req.getContentType());

        if ("video".equals(req.getContentType())) lesson.setContentUrl(req.getContentUrl());
        if ("text".equals(req.getContentType())) lesson.setContentText(req.getTextContent());

        Lesson updatedLesson = lessonService.updateLesson(id, lesson, teacherEmail, null);
        return ResponseEntity.ok(updatedLesson);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable String id,
            @RequestHeader("email") String teacherEmail) {
        lessonService.deleteLesson(id, teacherEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Lesson>> getLessonsByModule(@PathVariable String moduleId) {
        List<Lesson> lessons = lessonService.getLessonsByModule(moduleId);
        return ResponseEntity.ok(lessons);
    }
}
