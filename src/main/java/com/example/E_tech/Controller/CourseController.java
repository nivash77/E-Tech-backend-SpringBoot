package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Course;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Service.CourseService;
import com.example.E_tech.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> createCourse(@RequestHeader(value = "email", required = false) String email,
                                          @RequestBody Course course) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email header is missing");
            }
            if (!userService.isTeacher(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teachers can create courses");
            }
            String teacherId = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            course.setTeacherId(teacherId);
            Course createdCourse = courseService.createCourse(course, course.getThumbnail());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@RequestHeader(value = "email", required = false) String email,
                                          @PathVariable String id,
                                          @RequestBody Course course) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email header is missing");
            }
            if (!userService.isTeacher(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teachers can update courses");
            }
            String teacherId = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            Course updatedCourse = courseService.updateCourse(id, course, teacherId);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@RequestHeader(value = "email", required = false) String email,
                                          @PathVariable String id) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email header is missing");
            }
            if (!userService.isTeacher(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teachers can delete courses");
            }
            String teacherId = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            courseService.deleteCourse(id, teacherId);
            return ResponseEntity.ok("Course deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById( @RequestHeader("email") String email,@PathVariable String id){
        try{
            if(id==null || id.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Id is required");
            }
            User user=userService.findByEmailWithoutOptional(email);
            if(user==null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found");
            }
            Course course=courseService.getCourseById(id);
            if(course==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
            }
            return ResponseEntity.ok().body(course);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/courseOfTeacher/all")
    public ResponseEntity<?> getCoursesTeacher(@RequestHeader("email") String email) {
        try {
            var courses = courseService.getCoursesByTeacherEmail(email);
            return ResponseEntity.ok(courses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/enroll")
    public ResponseEntity<List<Course>> getUserEnrolledCourses(@RequestHeader("email") String email) {
        List<Course> enrolledCourses = courseService.getUserEnrolledCourses(email);
        return ResponseEntity.ok(enrolledCourses);
    }
}
