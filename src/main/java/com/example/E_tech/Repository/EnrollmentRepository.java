// EnrollmentRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    List<Enrollment> findByUserId(String userId);
    List<Enrollment> findByCourseId(String courseId);
    Optional<Enrollment> findByUserIdAndCourseId(String userId, String courseId);
}
