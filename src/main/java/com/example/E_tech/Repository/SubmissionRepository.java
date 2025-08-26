// SubmissionRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Submission;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'assignments', localField: 'assignmentId', foreignField: '_id', as: 'assignment' } }",
            "{ $unwind: '$assignment' }",
            "{ $match: { 'assignment.teacherId': ?0, grade: null } }"
    })
    List<Submission> findSubmissionsByTeacherIdAndGradeIsNull(String teacherId);
    List<Submission> findByAssignmentId(String assignmentId);
    List<Submission> findByStudentId(String studentId);
}
