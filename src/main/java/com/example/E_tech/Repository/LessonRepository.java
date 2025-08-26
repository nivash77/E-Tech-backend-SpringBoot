// LessonRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Lesson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findByModuleId(String moduleId);

    @Query("{ 'moduleId' : { $in: ?0 } }")
    List<Lesson> findByModuleIdIn(List<String> moduleIds);

}
