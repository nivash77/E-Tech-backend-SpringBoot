package com.example.E_tech.Service;

import com.example.E_tech.Entity.Discussion;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.CourseRepository;
import com.example.E_tech.Repository.DiscussionRepository;
import com.example.E_tech.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;


    private User requireUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing user"));
    }

    private void requireCourseExists(String courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private Discussion requireDiscussion(String id) {
        return discussionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion not found"));
    }

    private boolean isTeacherOrAdmin(User u) {
        String role = u.getRole() == null ? "" : u.getRole();
        return role.equalsIgnoreCase("TEACHER") || role.equalsIgnoreCase("ADMIN");
    }



    public Map<String, Object> mapDiscussionToDto(Discussion d) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", d.getId());
        dto.put("content", d.getContent());
        dto.put("likes", d.getLikes());
        dto.put("likedUserIds", d.getLikedUserIds());
        dto.put("createdAt", d.getCreatedAt());
        dto.put("updatedAt", d.getUpdatedAt());
        // Map userId to email
        dto.put("userEmail", userRepository.findById(d.getUserId())
                .map(User::getEmail)
                .orElse("Unknown"));
        return dto;
    }

    private List<Map<String, Object>> mapDiscussions(List<Discussion> discussions) {
        return discussions.stream()
                .map(this::mapDiscussionToDto)
                .toList();
    }



    // Top-level discussions for a course (paged)
    public Page<Map<String, Object>> getDiscussions(String courseId, int page, int size) {
        requireCourseExists(courseId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Discussion> discussions = discussionRepository.findByCourseIdAndParentIdIsNullOrderByCreatedAtDesc(courseId, pageable);
        return discussions.map(this::mapDiscussionToDto);
    }

    // Replies for a post
    public List<Map<String, Object>> getReplies(String parentId) {
        requireDiscussion(parentId);
        List<Discussion> replies = discussionRepository.findByParentIdOrderByCreatedAtAsc(parentId);
        return mapDiscussions(replies);
    }

    // Thread (post + replies)
    public Map<String, Object> getThread(String id) {
        Discussion root = requireDiscussion(id);
        List<Map<String, Object>> replies = getReplies(id);
        Map<String, Object> thread = new HashMap<>();
        thread.put("post", mapDiscussionToDto(root));
        thread.put("replies", replies);
        return thread;
    }


    // Create a top-level post
    public Discussion createPost(Discussion payload, String email) {
        User user = requireUserByEmail(email);
        if (payload.getCourseId() == null || payload.getCourseId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId is required");
        }
        if (payload.getContent() == null || payload.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }

        requireCourseExists(payload.getCourseId());

        Discussion d = new Discussion();
        d.setCourseId(payload.getCourseId());
        d.setUserId(user.getId());
        d.setContent(payload.getContent());
        d.setParentId(null);
        d.setLikes(0);
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        return discussionRepository.save(d);
    }

    // Reply to a post
    public Discussion createReply(String parentId, Discussion payload, String email) {
        User user = requireUserByEmail(email);
        Discussion parent = requireDiscussion(parentId);
        if (payload.getContent() == null || payload.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }

        Discussion reply = new Discussion();
        reply.setCourseId(parent.getCourseId());
        reply.setUserId(user.getId());
        reply.setContent(payload.getContent());
        reply.setParentId(parentId);
        reply.setLikes(0);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        return discussionRepository.save(reply);
    }

    // Update own post/reply
    public Discussion updatePost(String id, String email, Discussion payload) {
        User user = requireUserByEmail(email);
        Discussion d = requireDiscussion(id);

        if (!d.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own post");
        }
        if (payload.getContent() == null || payload.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }

        d.setContent(payload.getContent());
        d.setUpdatedAt(LocalDateTime.now());
        return discussionRepository.save(d);
    }

    // Delete post/reply
    public void deletePost(String id, String email) {
        User user = requireUserByEmail(email);
        Discussion d = requireDiscussion(id);

        if (!d.getUserId().equals(user.getId()) && !isTeacherOrAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this post");
        }

        if (d.getParentId() == null) {
            List<Discussion> replies = discussionRepository.findByParentIdOrderByCreatedAtAsc(d.getId());
            discussionRepository.deleteAll(replies);
        }

        discussionRepository.delete(d);
    }

    // Toggle like/unlike
    public Discussion toggleLike(String id, String email) {
        User user = requireUserByEmail(email);
        Discussion d = requireDiscussion(id);

        if (d.getLikedUserIds().contains(user.getId())) {
            d.getLikedUserIds().remove(user.getId());
            d.setLikes(Math.max(0, d.getLikes() - 1));
        } else {
            d.getLikedUserIds().add(user.getId());
            d.setLikes(d.getLikes() + 1);
        }
        d.setUpdatedAt(LocalDateTime.now());
        return discussionRepository.save(d);
    }

    public long countRepliesForCourse(String courseId) {
        requireCourseExists(courseId);
        return discussionRepository.countByCourseIdAndParentIdIsNotNull(courseId);
    }

    public List<Discussion> getTopLikedPosts(String courseId, int limit) {
        requireCourseExists(courseId);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("likes").descending());
        return discussionRepository.findByCourseIdAndParentIdIsNull(courseId, pageable).getContent();
    }
}
