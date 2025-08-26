package com.example.E_tech.Service;

import com.example.E_tech.Entity.Submission;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.SubmissionRepository;
import com.example.E_tech.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private UserRepository userRepository;

    private final GridFsOperations gridFsOperations;

    @Autowired
    private UserService userService; // Assume this exists to validate roles

    // Add submission
    public Submission addSubmission(String email, String assignmentId, MultipartFile file) {
        String role = userService.getRoleByEmail(email);
        if (!"STUDENT".toLowerCase().equalsIgnoreCase(role.toLowerCase())) {
            throw new RuntimeException("Only students can submit assignments");
        }
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        String fileId = storeFile(file);

        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(user.getId());
        submission.setFileId(fileId);
        submission.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    // Update submission by student (with file update)
    public Submission updateSubmission(String email, String submissionId, MultipartFile file) throws IOException {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        if (!submission.getStudentId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own submission");
        }

        //Delete old file from GridFS
        if (submission.getFileId() != null) {
            gridFsOperations.delete(new Query(Criteria.where("_id").is(submission.getFileId())));
        }

        //Upload new file to GridFS
        String newFileId = storeFile(file);

        submission.setFileId(newFileId);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    // Delete submission by student (with file deletion)
    public void deleteSubmission(String email, String submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (!submission.getStudentId().equals(email)) {
            throw new RuntimeException("You can only delete your own submission");
        }

        // Delete associated file from GridFS
        if (submission.getFileId() != null) {
            gridFsOperations.delete(new Query(Criteria.where("_id").is(submission.getFileId())));
        }

        submissionRepository.delete(submission);
    }




    // Teacher grades submission
    public Submission gradeSubmission(String email, String submissionId, Integer grade, String feedback) {
        String role = userService.getRoleByEmail(email);
        if (!"TEACHER".toLowerCase().equalsIgnoreCase(role.toLowerCase())) {
            throw new RuntimeException("Only teachers can grade submissions");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return submissionRepository.save(submission);
    }

    // Get submissions for assignment (teacher only)
    public List<Submission> getSubmissionsByAssignment(String email, String assignmentId) {
        String role = userService.getRoleByEmail(email);
        if (!"TEACHER".toLowerCase().equalsIgnoreCase(role.toLowerCase())) {
            throw new RuntimeException("Only teachers can view submissions");
        }
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    //Get student submissions
    public List<Submission> getStudentSubmissions(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return submissionRepository.findByStudentId(user.getId());
    }

    //File upload helper
    private String storeFile(MultipartFile file) {
        try {
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            return fileId.toHexString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    public Submission getSubmissionById(String id,String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return submissionRepository.findById(id).orElse(null);
    }
}
