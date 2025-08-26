// QuizRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
    List<Quiz> findByModuleId(String moduleId);
}
