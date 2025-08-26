// CourseRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByTeacherId(String teacherId);
    List<Course> findByEnrollmentsUserId(String userId);
    Optional<Course> findById(String id);
}
