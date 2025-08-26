package com.example.E_tech.Service;

import com.example.E_tech.Entity.Course;
import com.example.E_tech.Entity.Enrollment;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.CourseRepository;
import com.example.E_tech.Repository.EnrollmentRepository;
import com.example.E_tech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;

    public Course createCourse(Course course, String thumbnail) {
        User teacher = userRepository.findById(course.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        course.setTeacherId(teacher.getId());
        course.setCreatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course course, String teacherId) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!existing.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("You are not allowed to update this course");
        }
        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        existing.setPrice(course.getPrice());
        return courseRepository.save(existing);
    }

    public void deleteCourse(String id, String teacherId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("You are not allowed to delete this course");
        }
        courseRepository.delete(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }
    public List<Course> getCoursesByTeacherEmail(String email) {
        var teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!teacher.isVerified()) {
            throw new RuntimeException("Teacher email not verified");
        }

        return courseRepository.findByTeacherId(teacher.getId());
    }
    public List<Course> getUserEnrolledCourses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(user.getId());

        List<Course> enrolledCourses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            courseRepository.findById(enrollment.getCourseId())
                    .ifPresent(enrolledCourses::add);
        }

        return enrolledCourses;
    }
}
