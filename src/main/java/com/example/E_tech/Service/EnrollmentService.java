package com.example.E_tech.Service;

import com.example.E_tech.Entity.Course;
import com.example.E_tech.Entity.Enrollment;
import com.example.E_tech.Entity.Lesson;
import com.example.E_tech.Entity.Module;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.CourseRepository;
import com.example.E_tech.Repository.EnrollmentRepository;
import com.example.E_tech.Repository.LessonRepository;
import com.example.E_tech.Repository.ModuleRepository;
import com.example.E_tech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    // Enroll user in course
    public Enrollment enrollCourse(String userEmail, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"STUDENT".toLowerCase().equals(user.getRole().toLowerCase())) {
            throw new RuntimeException("Only students can enroll in courses");
        }

        if (enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId).isPresent()) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(user.getId());
        enrollment.setCourseId(courseId);
        enrollment.setCompletedLessons(new ArrayList<>());
        enrollment.setCompletedAssignments(new ArrayList<>());
        enrollment.setProgress(0.0);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Optional: Add enrollment to course
        course.addEnrollment(savedEnrollment);
        courseRepository.save(course);

        return savedEnrollment;
    }

    // Get enrollments by user
    public List<Enrollment> getEnrollmentsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return enrollmentRepository.findByUserId(user.getId());
    }

    // Get enrollments by course
    public List<Enrollment> getEnrollmentsByCourse(String courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    // Mark lesson as complete
    public Enrollment markLessonComplete(String userEmail, String lessonId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get lesson
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Get module
        Module module = moduleRepository.findById(lesson.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        String courseId = module.getCourseId();

        // Get enrollment for this user and course
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getCompletedLessons().contains(lessonId)) {
            enrollment.getCompletedLessons().add(lessonId);
        }

        recalcProgress(enrollment, courseId);

        return enrollmentRepository.save(enrollment);
    }

    // Mark assignment as complete
    public Enrollment markAssignmentComplete(String userEmail, String courseId, String assignmentId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getCompletedAssignments().contains(assignmentId)) {
            enrollment.getCompletedAssignments().add(assignmentId);
        }

        recalcProgress(enrollment, courseId);

        return enrollmentRepository.save(enrollment);
    }

    // Recalculate progress based on lessons completed
    private void recalcProgress(Enrollment enrollment, String courseId) {
        // Get all modules for course
        List<Module> modules = moduleRepository.findByCourseId(courseId);

        // Collect all module IDs
        List<String> moduleIds = new ArrayList<>();
        for (Module m : modules) moduleIds.add(m.getId());

        // Get all lessons for all modules
        List<Lesson> allLessons = lessonRepository.findByModuleIdIn(moduleIds);

        int totalLessons = allLessons.size();
        int completedLessons = enrollment.getCompletedLessons().size();

        double progress = totalLessons == 0 ? 0 : ((double) completedLessons / totalLessons) * 100;
        enrollment.setProgress(progress);
    }

    // Get progress for a user in a course
    public double getProgress(String userEmail, String courseId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        return enrollment.getProgress();
    }

    // Get all lessons for a course (via modules)
    public List<Lesson> getLessonsForCourse(String courseId) {
        List<Module> modules = moduleRepository.findByCourseId(courseId);
        List<String> moduleIds = new ArrayList<>();
        for (Module m : modules) moduleIds.add(m.getId());
        return lessonRepository.findByModuleIdIn(moduleIds);
    }
    public Enrollment updateQuizScore(String email, String moduleId, String quizId, int score) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Module module=moduleRepository.findById(moduleId).orElseThrow(()->new RuntimeException("module is not found"));
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), module.getCourseId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // Check if quiz already exists, update it
        Optional<Enrollment.QuizResult> existing = enrollment.getQuizResults()
                .stream()
                .filter(q -> q.getQuizId().equals(quizId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setScore(score);
        } else {
            enrollment.getQuizResults().add(new Enrollment.QuizResult(quizId, score));
        }

        return enrollmentRepository.save(enrollment);
    }

}
