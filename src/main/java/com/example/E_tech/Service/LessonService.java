package com.example.E_tech.Service;

import com.example.E_tech.Entity.Lesson;
import com.example.E_tech.Repository.LessonRepository;
import com.example.E_tech.Repository.ModuleRepository;
import com.example.E_tech.Repository.CourseRepository;
import com.example.E_tech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    public Lesson createLesson(Lesson lesson, String teacherEmail) {
        var module = moduleRepository.findById(lesson.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        var course = courseRepository.findById(module.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!course.getTeacherId().equals(teacher.getId()))
            throw new RuntimeException("Unauthorized: You cannot create lessons for this course");

        if (!teacher.isVerified()) throw new RuntimeException("User email not verified");

        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(String id, Lesson lesson, String teacherEmail, MultipartFile file) {
        Lesson existing = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        validateTeacherAccess(existing.getModuleId(), teacherEmail);

        existing.setTitle(lesson.getTitle());
        existing.setContentType(lesson.getContentType());

        if ("text".equals(lesson.getContentType())) {
            existing.setContentText(lesson.getContentText());
            existing.setContentUrl(null);
        } else if ("video".equals(lesson.getContentType())) {
            existing.setContentUrl(lesson.getContentUrl());
            existing.setContentText(null);
        } else if ("pdf".equals(lesson.getContentType()) && file != null && !file.isEmpty()) {
            try {
                String uploadedUrl = fileService.uploadFile(file);
                existing.setContentUrl(uploadedUrl);
                existing.setContentText(null);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        return lessonRepository.save(existing);
    }

    public void deleteLesson(String id, String teacherEmail) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        validateTeacherAccess(lesson.getModuleId(), teacherEmail);
        lessonRepository.delete(lesson);
    }

    public List<Lesson> getLessonsByModule(String moduleId) {
        return lessonRepository.findByModuleId(moduleId);
    }

    private void validateTeacherAccess(String moduleId, String teacherEmail) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        var course = courseRepository.findById(module.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!course.getTeacherId().equals(teacher.getId()))
            throw new RuntimeException("Unauthorized: You cannot manage lessons for this course");

        if (!teacher.isVerified()) throw new RuntimeException("User is not verified");
    }
}
