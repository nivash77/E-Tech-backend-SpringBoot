// NotificationRepository.java
package com.example.E_tech.Repository;

import com.example.E_tech.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    Page<Notification> findByUserId(String userId, Pageable pageable);


}
