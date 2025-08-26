// ModuleRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Module;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends MongoRepository<Module, String> {
    List<Module> findByCourseIdOrderByOrderAsc(String courseId);
    List<Module> findByCourseIdIn(List<String> courseIds);
    List<Module> findByCourseId(String courseId);
}
