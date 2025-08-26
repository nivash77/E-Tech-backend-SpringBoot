// DiscussionRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends MongoRepository<Discussion, String> {

    Page<Discussion> findByCourseIdAndParentIdIsNullOrderByCreatedAtDesc(String courseId, Pageable pageable);

    List<Discussion> findByParentIdOrderByCreatedAtAsc(String parentId);


    boolean existsByIdAndUserId(String id, String userId);
    Page<Discussion> findByCourseIdAndParentIdIsNull(String courseId, Pageable pageable);

    // Replies for a given post


    // All posts for a course
    List<Discussion> findByCourseId(String courseId);

    // Exists check by post ID and user ID (for ownership validation)


    // Count replies for a course (parentId is not null)
    long countByCourseIdAndParentIdIsNotNull(String courseId);

    // Find top liked posts in a course (parentId is null) with paging
    Page<Discussion> findByCourseIdAndParentIdIsNullOrderByLikesDesc(String courseId, Pageable pageable);
}
