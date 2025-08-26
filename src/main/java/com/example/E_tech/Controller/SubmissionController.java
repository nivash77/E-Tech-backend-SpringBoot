package com.example.E_tech.Controller;

import com.example.E_tech.Entity.Submission;
import com.example.E_tech.Service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    //Add a submission
    @PostMapping("/add")
    public ResponseEntity<Submission> addSubmission(
            @RequestHeader("email") String email,
            @RequestParam String assignmentId,
            @RequestParam MultipartFile file) {
        Submission submission=submissionService.addSubmission(email,assignmentId,file);
        return ResponseEntity.ok().body(submission);
    }

    //Update submission by student
    @PutMapping("/update/{id}")
    public ResponseEntity<Submission> updateSubmission(
            @RequestHeader("email") String email,
            @PathVariable String id,
            @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(submissionService.updateSubmission(email, id, file));
    }

    // Delete submission by student
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSubmission(
            @RequestHeader("email") String email,
            @PathVariable String id) {
        submissionService.deleteSubmission(email, id);
        return ResponseEntity.ok("Submission deleted successfully");
    }

    // Teacher updates grade & feedback
    @PutMapping("/grade/{id}")
    public ResponseEntity<Submission> gradeSubmission(
            @RequestHeader("email") String email,
            @PathVariable String id,
            @RequestParam Integer grade,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(submissionService.gradeSubmission(email, id, grade, feedback));
    }

    //Get all submissions for an assignment (teacher only)
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<Submission>> getSubmissionsByAssignment(
            @RequestHeader("email") String email,
            @PathVariable String assignmentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(email, assignmentId));
    }


    //Get all submissions of student
    @GetMapping("/student")
    public ResponseEntity<List<Submission>> getStudentSubmissions(@RequestHeader("email") String email) {
        return ResponseEntity.ok(submissionService.getStudentSubmissions(email));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable String id,@RequestHeader("email") String email) {
        Submission submission = submissionService.getSubmissionById(id,email);
        return ResponseEntity.ok(submission);
    }
}
