package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Enrollment;
import com.example.E_tech.Service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enrollCourse(
            @RequestParam String courseId,
            @RequestHeader("email") String useremail) {
        Enrollment enrollment = enrollmentService.enrollCourse(useremail, courseId);
        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Enrollment>> getUserEnrollments(@RequestHeader("email") String userId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByUser(userId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getCourseEnrollments(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }
    @PostMapping("/lesson/complete")
    public ResponseEntity<Enrollment> completeLesson(
            @RequestParam String courseId,
            @RequestParam String lessonId,
            @RequestHeader("email") String userEmail) {
        return ResponseEntity.ok(enrollmentService.markLessonComplete(userEmail, lessonId));
    }

    @PostMapping("/assignment/complete")
    public ResponseEntity<Enrollment> completeAssignment(
            @RequestParam String courseId,
            @RequestParam String assignmentId,
            @RequestHeader("email") String userEmail) {
        return ResponseEntity.ok(enrollmentService.markAssignmentComplete(userEmail, courseId, assignmentId));
    }

    @GetMapping("/progress")
    public ResponseEntity<Double> getProgress(
            @RequestParam String courseId,
            @RequestHeader("email") String userEmail) {
        return ResponseEntity.ok(enrollmentService.getProgress(userEmail, courseId));
    }
    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submitQuiz(
            @RequestHeader("email") String email,
            @RequestParam String courseId,
            @RequestParam String quizId,
            @RequestParam int score) {

        Enrollment enrollment = enrollmentService.updateQuizScore(email, courseId, quizId, score);
        return ResponseEntity.ok(enrollment);
    }

}
