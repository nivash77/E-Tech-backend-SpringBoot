package com.example.E_tech.Repository;


import com.example.E_tech.Entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment,String> {
    List<Assignment> findByModuleIdIn(List<String> moduleIds);
    List<Assignment> findByTeacherId(String teacherId);

}
