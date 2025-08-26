package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Assignment;
import com.example.E_tech.Entity.Course;
import com.example.E_tech.Service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/add")
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment,
                                              @RequestHeader("email") String email) {
        try {

            Assignment createdAssignment = assignmentService.createAssignment(assignment, email);
            return ResponseEntity.ok(createdAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable String id,
                                              @RequestBody Assignment assignment,
                                              @RequestHeader("email") String email) {
        try {
            Assignment updatedAssignment = assignmentService.updateAssignment(id, assignment,email);
            return ResponseEntity.ok(updatedAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable String id,
                                              @RequestHeader("email") String email) {
        try {
            assignmentService.deleteAssignment(id, email);
            return ResponseEntity.ok("Assignment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable String id) {
        try {
            Assignment assignment = assignmentService.getAssignmentById(id);
            return ResponseEntity.ok(assignment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/deadlines")
    public ResponseEntity<?> getUpcomingDeadlines(@RequestHeader("email") String email) {
        try {
            return ResponseEntity.ok(assignmentService.getUpcomingDeadlines(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAssignments(@RequestHeader("email") String email) {
        try {
            List<Assignment> pending = assignmentService.getPendingAssignments(email);
            return ResponseEntity.ok(pending);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    @GetMapping("/couresAssignment")
//    public ResponseEntity<?> getAllCoursesTeachers(@RequestHeader("email") String email){
//        try {
//            List<Course> courses=assignmentService
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/getAssignmentofTeacher")
    public ResponseEntity<?> getAssignmentOfTeacher(@RequestHeader("email") String email){
        try {
            List<Assignment> assignments = assignmentService.getAssignmentOfTeacher(email);
            return ResponseEntity.ok(assignments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
